import json, pathlib, sys
root = pathlib.Path(r"d:\AI_Personal\AI\DFS")
stories = json.loads((root / "app/src/main/assets/seed/stories.json").read_text(encoding="utf-8"))
errors = []
for s in stories:
    ids = {sc["id"] for sc in s["scenes"]}
    ends = {sc["id"] for sc in s["scenes"] if not sc["choices"]}
    if not ends: errors.append(f"{s['id']}: no ending")
    for sc in s["scenes"]:
        for c in sc["choices"]:
            if c["next"] not in ids: errors.append(f"{s['id']}/{sc['id']}/{c['id']}: bad next {c['next']}")
            for k in ("id","text","subtitle","points","tag","feedback","next"):
                if k not in c: errors.append(f"{s['id']}/{sc['id']}/{c['id']}: missing {k}")
    # every path reaches an end
    def walk(sid, seen):
        sc = next(x for x in s["scenes"] if x["id"] == sid)
        if not sc["choices"]: return
        if sid in seen: errors.append(f"{s['id']}: cycle at {sid}"); return
        for c in sc["choices"]: walk(c["next"], seen | {sid})
    walk(s["scenes"][0]["id"], set())
if errors:
    print("\n".join(errors)); sys.exit(1)

out = ["-- =====================================================================",
       "-- Drug-Free India · Extra stories                (file 6, optional)",
       "-- Run AFTER 04_seed.sql. Adds the 6 additional single-player scenarios that",
       "-- ship in the app (assets/seed/stories.json) so the server can score them.",
       "-- Generated from the bundled JSON; safe to re-run (ON CONFLICT DO NOTHING).",
       "-- =====================================================================", ""]
for s in stories:
    if s["id"] == "farewell_party": continue
    def q(v): return "'" + v.replace("'", "''") + "'"
    out.append(f"insert into public.stories (id, title, pack, description, estimated_seconds, sort_order, cover_image_key, version, is_published, scenes)")
    out.append(f"values ({q(s['id'])}, {q(s['title'])}, {q(s['pack'])}, {q(s['description'])}, {s['estimated_seconds']}, {s['sort_order']}, {q(s['cover_image_key'])}, {s['version']}, true,")
    out.append("  $json$" + json.dumps(s["scenes"], ensure_ascii=False, indent=1) + "$json$::jsonb)")
    out.append("on conflict (id) do nothing;\n")
(root / "supabase/06_more_stories.sql").write_text("\n".join(out), encoding="utf-8")
print("OK", len(stories), "stories valid; wrote 06_more_stories.sql")
