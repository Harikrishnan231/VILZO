-- =====================================================================
-- Drug-Free India · Extra stories                (file 6, optional)
-- Run AFTER 04_seed.sql. Adds the 6 additional single-player scenarios that
-- ship in the app (assets/seed/stories.json) so the server can score them.
-- Generated from the bundled JSON; safe to re-run (ON CONFLICT DO NOTHING).
-- =====================================================================

insert into public.stories (id, title, pack, description, estimated_seconds, sort_order, cover_image_key, version, is_published, scenes)
values ('group_chat_dare', 'Group Chat Dare', 'online', 'A late-night dare lands in your batch group. Practise setting a digital boundary.', 30, 2, 'story_group_chat_cover', 1, true,
  $json$[
 {
  "id": "s1",
  "imageKey": "story_group_chat_cover",
  "narration": "A late-night ping in your batch group chat.",
  "speaker": "Class group",
  "quote": "Who's brave enough to try it tomorrow at the back bench? Reply with a fire emoji!",
  "timerSeconds": 20,
  "pressure": 0.7,
  "choices": [
   {
    "id": "a",
    "text": "Reply: \"Not for me, folks.\"",
    "subtitle": "Short, clear and friendly",
    "points": 6,
    "tag": "calm_no",
    "feedback": "A short, friendly reply often gives quieter classmates permission to say no too.",
    "next": "s2"
   },
   {
    "id": "b",
    "text": "Mute the chat for the night",
    "subtitle": "Step away from the pressure",
    "points": 5,
    "tag": "exit_plan",
    "feedback": "Digital boundaries count. Not reacting takes away the audience.",
    "next": "s2"
   },
   {
    "id": "c",
    "text": "Send a laughing emoji to blend in",
    "subtitle": "Avoid standing out",
    "points": 0,
    "tag": "gave_in",
    "feedback": "Blending in is a common reflex. Even a reaction can keep a dare going. Let's look at other options.",
    "next": "s2"
   }
  ]
 },
 {
  "id": "s2",
  "imageKey": "story_group_chat_cover",
  "narration": "Next morning, a classmate messages you privately.",
  "speaker": "Kavya",
  "quote": "I didn't want to do it either. What should I say?",
  "timerSeconds": 20,
  "pressure": 0.4,
  "choices": [
   {
    "id": "a",
    "text": "Share a simple line",
    "subtitle": "\"Just say it's not your thing.\"",
    "points": 6,
    "tag": "calm_no",
    "feedback": "Helping a friend find their words is a strong, supportive move.",
    "next": "s3"
   },
   {
    "id": "b",
    "text": "Suggest doing something else together",
    "subtitle": "Plan lunch at the canteen",
    "points": 5,
    "tag": "redirect",
    "feedback": "A better plan makes it easy for both of you to skip the dare.",
    "next": "s3"
   },
   {
    "id": "c",
    "text": "Tell her to talk to someone she trusts",
    "subtitle": "A friend, sibling or mentor",
    "points": 5,
    "tag": "trusted_contact",
    "feedback": "Pointing someone toward trusted support is always a good option.",
    "next": "s3"
   }
  ]
 },
 {
  "id": "s3",
  "imageKey": "story_group_chat_cover",
  "narration": "The group chat moves on to exam notes.",
  "speaker": "You",
  "quote": "That was easier than I expected.",
  "takeaway": "You can set boundaries online just as clearly as in person.",
  "isEnd": true,
  "choices": []
 }
]$json$::jsonb)
on conflict (id) do nothing;

insert into public.stories (id, title, pack, description, estimated_seconds, sort_order, cover_image_key, version, is_published, scenes)
values ('hostel_night', 'Hostel Night', 'hostel', 'A roommate offers a shortcut for an all-nighter. Practise choosing your own routine.', 35, 3, 'story_hostel_cover', 1, true,
  $json$[
 {
  "id": "s1",
  "imageKey": "story_hostel_cover",
  "narration": "1:30 AM in the hostel corridor. Exams start tomorrow.",
  "speaker": "Vikrant",
  "quote": "Seniors say this helps you study all night. Want some?",
  "timerSeconds": 20,
  "pressure": 0.7,
  "choices": [
   {
    "id": "a",
    "text": "No thanks, I'll take a short nap",
    "subtitle": "Stick to your own routine",
    "points": 6,
    "tag": "calm_no",
    "feedback": "Saying no and naming your own plan makes the answer feel natural.",
    "next": "s2"
   },
   {
    "id": "b",
    "text": "Head to the quiet study hall",
    "subtitle": "Change your surroundings",
    "points": 5,
    "tag": "exit_plan",
    "feedback": "Moving to a different space is a simple way to step out of pressure.",
    "next": "s2"
   },
   {
    "id": "c",
    "text": "Maybe just a little to test it",
    "subtitle": "Go along with it",
    "points": 0,
    "tag": "gave_in",
    "feedback": "Never take something when you don't know what it is. Let's practise another way.",
    "next": "s2"
   }
  ]
 },
 {
  "id": "s2",
  "imageKey": "story_hostel_cover",
  "narration": "Vikrant follows you out, looking stressed.",
  "speaker": "Vikrant",
  "quote": "Honestly, I'm panicking about this paper.",
  "timerSeconds": 20,
  "pressure": 0.4,
  "choices": [
   {
    "id": "a",
    "text": "Offer to revise together",
    "subtitle": "Study side by side",
    "points": 6,
    "tag": "redirect",
    "feedback": "Turning pressure into a shared plan helps both of you.",
    "next": "s3"
   },
   {
    "id": "b",
    "text": "Suggest he call someone he trusts",
    "subtitle": "Family or a close friend",
    "points": 5,
    "tag": "trusted_contact",
    "feedback": "Talking to someone trusted can ease stress a lot.",
    "next": "s3"
   },
   {
    "id": "c",
    "text": "Say goodnight and go to sleep",
    "subtitle": "Look after yourself first",
    "points": 4,
    "tag": "exit_plan",
    "feedback": "Rest is a valid choice. Looking after yourself matters.",
    "next": "s3"
   }
  ]
 },
 {
  "id": "s3",
  "imageKey": "story_hostel_cover",
  "narration": "Morning. You walk into the exam hall rested.",
  "speaker": "You",
  "quote": "My own routine works for me.",
  "takeaway": "Your own routine is a choice you can stand by.",
  "isEnd": true,
  "choices": []
 }
]$json$::jsonb)
on conflict (id) do nothing;

insert into public.stories (id, title, pack, description, estimated_seconds, sort_order, cover_image_key, version, is_published, scenes)
values ('after_the_match', 'After the Match', 'sports', 'Your team wins the trophy and the celebration gets pushy. Practise staying true to your game.', 30, 4, 'story_after_match_cover', 1, true,
  $json$[
 {
  "id": "s1",
  "imageKey": "story_after_match_cover",
  "narration": "Your team just won the inter-college trophy.",
  "speaker": "Captain",
  "quote": "Champs celebrate hard! Join in or you're not really one of us.",
  "timerSeconds": 20,
  "pressure": 0.7,
  "choices": [
   {
    "id": "a",
    "text": "I'm good, I'll celebrate my way",
    "subtitle": "Firm and friendly",
    "points": 6,
    "tag": "calm_no",
    "feedback": "You celebrated the win and kept your choice. Both can be true.",
    "next": "s2"
   },
   {
    "id": "b",
    "text": "Let's all get food instead",
    "subtitle": "Suggest a team dinner",
    "points": 5,
    "tag": "redirect",
    "feedback": "A team dinner keeps the celebration going without the pressure.",
    "next": "s2"
   },
   {
    "id": "c",
    "text": "My family is waiting at the gate",
    "subtitle": "Head out gracefully",
    "points": 5,
    "tag": "exit_plan",
    "feedback": "A graceful exit lets you leave on a high note.",
    "next": "s2"
   }
  ]
 },
 {
  "id": "s2",
  "imageKey": "story_after_match_cover",
  "narration": "A teammate catches up with you later.",
  "speaker": "Teammate",
  "quote": "Was the captain annoyed? I didn't want to join either.",
  "timerSeconds": 20,
  "pressure": 0.3,
  "choices": [
   {
    "id": "a",
    "text": "\"It's fine. We can say no and still be a team.\"",
    "subtitle": "Reassure them",
    "points": 6,
    "tag": "calm_no",
    "feedback": "Showing that a no doesn't break friendships is powerful.",
    "next": "s3"
   },
   {
    "id": "b",
    "text": "Plan a practice session together",
    "subtitle": "Focus on the next match",
    "points": 5,
    "tag": "redirect",
    "feedback": "Shared goals keep a team close.",
    "next": "s3"
   }
  ]
 },
 {
  "id": "s3",
  "imageKey": "story_after_match_cover",
  "narration": "Monday practice. The team is back on the field.",
  "speaker": "Coach",
  "quote": "Good focus today, everyone.",
  "takeaway": "You can celebrate a win and still make your own choices.",
  "isEnd": true,
  "choices": []
 }
]$json$::jsonb)
on conflict (id) do nothing;

insert into public.stories (id, title, pack, description, estimated_seconds, sort_order, cover_image_key, version, is_published, scenes)
values ('cousins_wedding', 'Cousin''s Wedding', 'family', 'Older cousins push you at a family function. Practise a respectful exit.', 35, 5, 'story_wedding_cover', 1, true,
  $json$[
 {
  "id": "s1",
  "imageKey": "story_wedding_cover",
  "narration": "Behind the wedding tent, older cousins call you over.",
  "speaker": "Cousin",
  "quote": "You're in college now. Try it, nobody inside will know!",
  "timerSeconds": 20,
  "pressure": 0.6,
  "choices": [
   {
    "id": "a",
    "text": "No thanks, bhai",
    "subtitle": "Short and respectful",
    "points": 6,
    "tag": "calm_no",
    "feedback": "A short, respectful no works with family too.",
    "next": "s2"
   },
   {
    "id": "b",
    "text": "Uncle asked me to help with the music",
    "subtitle": "Use a family duty",
    "points": 5,
    "tag": "exit_plan",
    "feedback": "A real task is a natural reason to step away.",
    "next": "s2"
   },
   {
    "id": "c",
    "text": "Let's go get kulfi instead!",
    "subtitle": "Bring them back to the party",
    "points": 5,
    "tag": "redirect",
    "feedback": "Redirecting back to the celebration keeps the mood happy.",
    "next": "s2"
   }
  ]
 },
 {
  "id": "s2",
  "imageKey": "story_wedding_cover",
  "narration": "Your younger cousin saw what happened.",
  "speaker": "Younger cousin",
  "quote": "What would you do if they ask me next year?",
  "timerSeconds": 20,
  "pressure": 0.3,
  "choices": [
   {
    "id": "a",
    "text": "Teach a simple line",
    "subtitle": "\"Not for me, thanks.\"",
    "points": 6,
    "tag": "calm_no",
    "feedback": "You just passed on a skill. That's leadership.",
    "next": "s3"
   },
   {
    "id": "b",
    "text": "\"You can always come find me.\"",
    "subtitle": "Be a trusted person",
    "points": 6,
    "tag": "trusted_contact",
    "feedback": "Being someone's trusted person is a big deal.",
    "next": "s3"
   }
  ]
 },
 {
  "id": "s3",
  "imageKey": "story_wedding_cover",
  "narration": "Back on the dance floor with the family.",
  "speaker": "You",
  "quote": "That felt good.",
  "takeaway": "A respectful no protects both you and your relationships.",
  "isEnd": true,
  "choices": []
 }
]$json$::jsonb)
on conflict (id) do nothing;

insert into public.stories (id, title, pack, description, estimated_seconds, sort_order, cover_image_key, version, is_published, scenes)
values ('the_cool_senior', 'The Cool Senior', 'campus', 'A senior you admire sets a condition for joining their team. Practise a professional boundary.', 35, 6, 'story_senior_cover', 1, true,
  $json$[
 {
  "id": "s1",
  "imageKey": "story_senior_cover",
  "narration": "A respected senior invites you to their project team.",
  "speaker": "Senior",
  "quote": "We all do this before brainstorming. Join us if you want to be in the core team.",
  "timerSeconds": 20,
  "pressure": 0.7,
  "choices": [
   {
    "id": "a",
    "text": "I'd love to join, but that's not for me",
    "subtitle": "Keep it about your skills",
    "points": 6,
    "tag": "calm_no",
    "feedback": "You showed interest in the work and kept your boundary. That's professional.",
    "next": "s2"
   },
   {
    "id": "b",
    "text": "Can we brainstorm in the lab?",
    "subtitle": "Suggest another way",
    "points": 5,
    "tag": "redirect",
    "feedback": "Offering a different setting keeps the door open.",
    "next": "s2"
   },
   {
    "id": "c",
    "text": "I have a deadline, see you later",
    "subtitle": "Step away for now",
    "points": 4,
    "tag": "exit_plan",
    "feedback": "Stepping away avoids being cornered. You can follow up later.",
    "next": "s2"
   }
  ]
 },
 {
  "id": "s2",
  "imageKey": "story_senior_cover",
  "narration": "The next day, the senior messages you.",
  "speaker": "Senior",
  "quote": "No pressure. Can you share your design ideas?",
  "timerSeconds": null,
  "pressure": 0.2,
  "choices": [
   {
    "id": "a",
    "text": "Share your ideas",
    "subtitle": "Let your work speak",
    "points": 6,
    "tag": "redirect",
    "feedback": "Good teams value skills, not going along.",
    "next": "s3"
   },
   {
    "id": "b",
    "text": "Ask a mentor for advice first",
    "subtitle": "Get a second opinion",
    "points": 5,
    "tag": "trusted_contact",
    "feedback": "Checking with someone you trust is a smart move.",
    "next": "s3"
   }
  ]
 },
 {
  "id": "s3",
  "imageKey": "story_senior_cover",
  "narration": "Your design makes it into the final project.",
  "speaker": "You",
  "quote": "My work speaks for itself.",
  "takeaway": "Real respect comes from your skills, not from going along.",
  "isEnd": true,
  "choices": []
 }
]$json$::jsonb)
on conflict (id) do nothing;

insert into public.stories (id, title, pack, description, estimated_seconds, sort_order, cover_image_key, version, is_published, scenes)
values ('stressed_before_exams', 'Stressed Before Exams', 'exams', 'Someone offers a quick fix for exam nerves. Practise healthier ways to cope.', 35, 7, 'story_exam_cover', 1, true,
  $json$[
 {
  "id": "s1",
  "imageKey": "story_exam_cover",
  "narration": "Your hands are shaking before the hardest paper.",
  "speaker": "Senior",
  "quote": "You look stressed. Take this, it calms you down instantly.",
  "timerSeconds": 20,
  "pressure": 0.7,
  "choices": [
   {
    "id": "a",
    "text": "No thanks, I'll take a few slow breaths",
    "subtitle": "Use your own way to calm down",
    "points": 6,
    "tag": "calm_no",
    "feedback": "Slow breathing and water are simple ways many people use to settle nerves.",
    "next": "s2"
   },
   {
    "id": "b",
    "text": "Call my sister for a minute",
    "subtitle": "Reach out to someone you trust",
    "points": 6,
    "tag": "trusted_contact",
    "feedback": "Hearing a familiar voice can be very steadying.",
    "next": "s2"
   },
   {
    "id": "c",
    "text": "Take it, I'm too nervous",
    "subtitle": "Accept the quick fix",
    "points": 0,
    "tag": "gave_in",
    "feedback": "Exam nerves are very common. Never take something unknown. Let's practise other ways to cope.",
    "next": "s2"
   }
  ]
 },
 {
  "id": "s2",
  "imageKey": "story_exam_cover",
  "narration": "Ten minutes until the exam starts.",
  "speaker": "You",
  "quote": "What helps me focus right now?",
  "timerSeconds": null,
  "pressure": 0.3,
  "choices": [
   {
    "id": "a",
    "text": "Quickly review my summary notes",
    "subtitle": "Trust your preparation",
    "points": 5,
    "tag": "redirect",
    "feedback": "Focusing on what you know builds calm confidence.",
    "next": "s3"
   },
   {
    "id": "b",
    "text": "Walk and drink some water",
    "subtitle": "Reset your body",
    "points": 5,
    "tag": "exit_plan",
    "feedback": "A short walk is a simple reset.",
    "next": "s3"
   }
  ]
 },
 {
  "id": "s3",
  "imageKey": "story_exam_cover",
  "narration": "You finish the paper and step out into the sun.",
  "speaker": "You",
  "quote": "I handled that my way.",
  "takeaway": "Healthy ways to cope with stress get stronger every time you use them.",
  "isEnd": true,
  "choices": []
 }
]$json$::jsonb)
on conflict (id) do nothing;
