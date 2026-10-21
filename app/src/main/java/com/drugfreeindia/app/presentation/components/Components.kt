package com.drugfreeindia.app.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.drugfreeindia.app.presentation.theme.DfiMotion
import com.drugfreeindia.app.presentation.theme.DfiTheme

// ---------------------------------------------------------------- Buttons

@Composable
private fun pressScale(source: MutableInteractionSource): Float {
    val pressed by source.collectIsPressedAsState()
    val s by animateFloatAsState(if (pressed) DfiMotion.pressedScale else 1f, DfiMotion.tweenOrSnap(DfiMotion.SHORT), label = "press")
    return s
}

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    icon: ImageVector? = null
) {
    val c = DfiTheme.colors
    val src = remember { MutableInteractionSource() }
    val active = enabled && !loading
    Surface(
        onClick = onClick,
        enabled = active,
        shape = DfiTheme.shapes.button,
        color = if (active) c.primary else c.disabled,
        contentColor = c.onPrimary,
        interactionSource = src,
        modifier = modifier.heightIn(min = 52.dp).scale(pressScale(src))
    ) {
        Row(Modifier.padding(horizontal = 20.dp, vertical = 14.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            if (loading) {
                CircularProgressIndicator(Modifier.size(18.dp), color = c.onPrimary, strokeWidth = 2.dp)
                Spacer(Modifier.width(10.dp))
            } else if (icon != null) {
                Icon(icon, null, Modifier.size(20.dp)); Spacer(Modifier.width(8.dp))
            }
            Text(text, style = DfiTheme.typography.label, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    icon: ImageVector? = null,
    contentColor: Color = DfiTheme.colors.primaryDark
) {
    val c = DfiTheme.colors
    val src = remember { MutableInteractionSource() }
    val active = enabled && !loading
    Surface(
        onClick = onClick, enabled = active, shape = DfiTheme.shapes.button, color = c.surface,
        contentColor = if (active) contentColor else c.disabled,
        border = BorderStroke(1.dp, if (active) c.outline else c.disabled), interactionSource = src,
        modifier = modifier.heightIn(min = 52.dp).scale(pressScale(src))
    ) {
        Row(Modifier.padding(horizontal = 18.dp, vertical = 14.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            if (loading) {
                CircularProgressIndicator(Modifier.size(18.dp), color = contentColor, strokeWidth = 2.dp)
                Spacer(Modifier.width(10.dp))
            } else if (icon != null) {
                Icon(icon, null, Modifier.size(20.dp)); Spacer(Modifier.width(8.dp))
            }
            Text(text, style = DfiTheme.typography.label, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
fun LinkButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, color: Color = DfiTheme.colors.primaryDark) {
    TextButton(onClick = onClick, modifier = modifier.heightIn(min = 48.dp)) {
        Text(text, style = DfiTheme.typography.labelSmall, color = color)
    }
}

// ---------------------------------------------------------------- Fields

@Composable
fun DfiTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    supporting: String? = null,
    error: String? = null,
    singleLine: Boolean = true,
    minLines: Int = 1,
    leading: (@Composable () -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    enabled: Boolean = true
) {
    val c = DfiTheme.colors
    Column(modifier) {
        Text(label, style = DfiTheme.typography.labelSmall, color = c.onSurface)
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = value, onValueChange = onValueChange, enabled = enabled,
            modifier = Modifier.fillMaxWidth().semantics { contentDescription = label },
            placeholder = placeholder?.let { { Text(it, style = DfiTheme.typography.body, color = c.onSurfaceVariant) } },
            textStyle = DfiTheme.typography.body, singleLine = singleLine, minLines = minLines,
            leadingIcon = leading, isError = error != null, shape = DfiTheme.shapes.input,
            keyboardOptions = keyboardOptions, keyboardActions = keyboardActions,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = c.primary, unfocusedBorderColor = c.outline,
                focusedContainerColor = c.surface, unfocusedContainerColor = c.surface, errorBorderColor = c.error
            )
        )
        val helper = error ?: supporting
        if (helper != null) {
            Spacer(Modifier.height(4.dp))
            Text(helper, style = DfiTheme.typography.caption, color = if (error != null) c.error else c.onSurfaceVariant)
        }
    }
}

@Composable
fun SearchField(value: String, onValueChange: (String) -> Unit, placeholder: String, modifier: Modifier = Modifier) {
    val c = DfiTheme.colors
    OutlinedTextField(
        value = value, onValueChange = onValueChange, singleLine = true,
        modifier = modifier.fillMaxWidth().semantics { contentDescription = placeholder },
        placeholder = { Text(placeholder, style = DfiTheme.typography.body, color = c.onSurfaceVariant, maxLines = 1) },
        leadingIcon = { Icon(Icons.Rounded.Search, null, tint = c.onSurfaceVariant) },
        textStyle = DfiTheme.typography.body, shape = DfiTheme.shapes.input,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = c.primary, unfocusedBorderColor = c.outline,
            focusedContainerColor = c.surface, unfocusedContainerColor = c.surface
        )
    )
}

// ---------------------------------------------------------------- Layout pieces

@Composable
fun TopBar(title: String, onBack: (() -> Unit)? = null, actions: @Composable RowScope.() -> Unit = {}) {
    Row(
        Modifier.fillMaxWidth().statusBarsPadding().heightIn(min = 56.dp).padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onBack != null) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Back", tint = DfiTheme.colors.onSurface) }
        } else Spacer(Modifier.width(16.dp))
        Text(
            title, style = DfiTheme.typography.sectionTitle, color = DfiTheme.colors.onSurface,
            maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f).semantics { heading() }
        )
        actions()
    }
}

@Composable
fun ScreenTitle(title: String, subtitle: String? = null, modifier: Modifier = Modifier) {
    Column(modifier) {
        Text(title, style = DfiTheme.typography.screenTitle, color = DfiTheme.colors.onSurface, modifier = Modifier.semantics { heading() })
        if (subtitle != null) {
            Spacer(Modifier.height(4.dp))
            Text(subtitle, style = DfiTheme.typography.bodySmall, color = DfiTheme.colors.onSurfaceVariant)
        }
    }
}

@Composable
fun SectionHeader(title: String, modifier: Modifier = Modifier, action: String? = null, onAction: (() -> Unit)? = null) {
    Row(modifier.fillMaxWidth().heightIn(min = 40.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(title, style = DfiTheme.typography.sectionTitle, color = DfiTheme.colors.onSurface,
            modifier = Modifier.weight(1f).semantics { heading() }, maxLines = 1, overflow = TextOverflow.Ellipsis)
        if (action != null && onAction != null) LinkButton(action, onAction)
    }
}

@Composable
fun DfiCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    color: Color = DfiTheme.colors.surface,
    padding: PaddingValues = PaddingValues(16.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = DfiTheme.shapes.card
    val border = BorderStroke(1.dp, DfiTheme.colors.outline)
    if (onClick != null) {
        val src = remember { MutableInteractionSource() }
        Surface(onClick = onClick, shape = shape, color = color, border = border, interactionSource = src,
            modifier = modifier.scale(pressScale(src))) { Column(Modifier.padding(padding), content = content) }
    } else {
        Surface(shape = shape, color = color, border = border, modifier = modifier) { Column(Modifier.padding(padding), content = content) }
    }
}

@Composable
fun IconBadge(icon: ImageVector, tint: Color = DfiTheme.colors.primaryDark, bg: Color = DfiTheme.colors.primarySoft, size: Dp = 40.dp) {
    Box(Modifier.size(size).clip(DfiTheme.shapes.tile).background(bg), contentAlignment = Alignment.Center) {
        Icon(icon, null, tint = tint, modifier = Modifier.size(size * 0.5f))
    }
}

@Composable
fun ListRow(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    icon: ImageVector? = null,
    iconTint: Color = DfiTheme.colors.primaryDark,
    iconBg: Color = DfiTheme.colors.primarySoft,
    trailing: (@Composable () -> Unit)? = { Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, null, tint = DfiTheme.colors.onSurfaceVariant) },
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier.fillMaxWidth().heightIn(min = 56.dp)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) { IconBadge(icon, iconTint, iconBg); Spacer(Modifier.width(14.dp)) }
        Column(Modifier.weight(1f)) {
            Text(title, style = DfiTheme.typography.cardTitle, color = DfiTheme.colors.onSurface)
            if (subtitle != null) Text(subtitle, style = DfiTheme.typography.bodySmall, color = DfiTheme.colors.onSurfaceVariant)
        }
        if (trailing != null) { Spacer(Modifier.width(8.dp)); trailing() }
    }
}

@Composable
fun SwitchRow(title: String, subtitle: String?, checked: Boolean, onChange: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    val c = DfiTheme.colors
    Row(
        modifier.fillMaxWidth().heightIn(min = 56.dp)
            .selectable(selected = checked, role = Role.Switch, onClick = { onChange(!checked) })
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(title, style = DfiTheme.typography.cardTitle, color = c.onSurface)
            if (subtitle != null) Text(subtitle, style = DfiTheme.typography.bodySmall, color = c.onSurfaceVariant)
        }
        Spacer(Modifier.width(12.dp))
        Switch(checked = checked, onCheckedChange = null, colors = SwitchDefaults.colors(
            checkedTrackColor = c.primary, uncheckedTrackColor = c.surfaceMuted, uncheckedThumbColor = c.disabled, uncheckedBorderColor = c.disabled))
    }
}

@Composable
fun Divider() = Box(Modifier.fillMaxWidth().height(1.dp).background(DfiTheme.colors.outline))

/** Small rounded label: "Demo", "Sample listing", "Verified", "Pending" … */
@Composable
fun Tag(text: String, modifier: Modifier = Modifier, color: Color = DfiTheme.colors.onSurfaceVariant, bg: Color = DfiTheme.colors.surfaceMuted) {
    Text(text, style = DfiTheme.typography.caption, color = color, maxLines = 1,
        modifier = modifier.clip(DfiTheme.shapes.chip).background(bg).padding(horizontal = 8.dp, vertical = 3.dp))
}

// ---------------------------------------------------------------- Tabs & chips

/** Single-line segmented control; labels never wrap. */
@Composable
fun SegmentedTabs(options: List<String>, selected: Int, onSelect: (Int) -> Unit, modifier: Modifier = Modifier) {
    val c = DfiTheme.colors
    Row(modifier.fillMaxWidth().clip(DfiTheme.shapes.input).background(c.surfaceMuted).padding(4.dp)) {
        options.forEachIndexed { i, label ->
            val sel = i == selected
            Box(
                Modifier.weight(1f).heightIn(min = 44.dp).clip(DfiTheme.shapes.tile)
                    .background(if (sel) c.surface else Color.Transparent)
                    .then(if (sel) Modifier.border(1.dp, c.outline, DfiTheme.shapes.tile) else Modifier)
                    .selectable(sel, role = Role.Tab) { onSelect(i) }
                    .padding(horizontal = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(label, style = DfiTheme.typography.labelSmall, color = if (sel) c.onSurface else c.onSurfaceVariant,
                    maxLines = 1, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center)
            }
        }
    }
}

@Composable
fun ChipRow(options: List<String>, selected: String?, onSelect: (String?) -> Unit, modifier: Modifier = Modifier, allLabel: String = "All") {
    LazyRow(modifier, horizontalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(horizontal = 20.dp)) {
        item { FilterPill(allLabel, selected == null) { onSelect(null) } }
        items(options) { o -> FilterPill(o, selected == o) { onSelect(o) } }
    }
}

@Composable
fun FilterPill(text: String, selected: Boolean, onClick: () -> Unit) {
    val c = DfiTheme.colors
    Box(
        Modifier.heightIn(min = 40.dp).clip(DfiTheme.shapes.pill)
            .background(if (selected) c.primarySoft else c.surface)
            .border(1.dp, if (selected) c.primary else c.outline, DfiTheme.shapes.pill)
            .selectable(selected, role = Role.RadioButton, onClick = onClick)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, style = DfiTheme.typography.labelSmall, color = if (selected) c.primaryDark else c.onSurface, maxLines = 1)
    }
}

// ---------------------------------------------------------------- Data viz

@Composable
fun ProgressRing(
    progress: Float,
    modifier: Modifier = Modifier,
    size: Dp = 120.dp,
    stroke: Dp = 10.dp,
    color: Color = DfiTheme.colors.primary,
    description: String? = null,
    content: @Composable () -> Unit = {}
) {
    val track = DfiTheme.colors.surfaceMuted
    val anim by animateFloatAsState(progress.coerceIn(0f, 1f), DfiMotion.tweenOrSnap(600), label = "ring")
    Box(modifier.size(size).semantics { if (description != null) stateDescription = description }, contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val s = Stroke(stroke.toPx(), cap = StrokeCap.Round)
            val inset = stroke.toPx() / 2
            val arcSize = androidx.compose.ui.geometry.Size(this.size.width - inset * 2, this.size.height - inset * 2)
            drawArc(track, 0f, 360f, false, Offset(inset, inset), arcSize, style = s)
            if (anim > 0f) drawArc(color, -90f, 360f * anim, false, Offset(inset, inset), arcSize, style = s)
        }
        content()
    }
}

/** Line chart with month labels. Null values are gaps; the chart never invents points. */
@Composable
fun TrendChart(points: List<Pair<String, Int?>>, modifier: Modifier = Modifier, maxValue: Int = 100) {
    val c = DfiTheme.colors
    val desc = points.joinToString { (m, v) -> "$m: ${v ?: "no data"}" }
    Column(modifier.semantics { contentDescription = "Trend chart. $desc" }) {
        Canvas(Modifier.fillMaxWidth().height(120.dp)) {
            val n = points.size
            if (n == 0) return@Canvas
            val stepX = if (n == 1) 0f else size.width / (n - 1)
            for (g in 0..2) {
                val y = size.height * g / 2f
                drawLine(c.outline, Offset(0f, y), Offset(size.width, y), 1.dp.toPx())
            }
            var path: Path? = null
            points.forEachIndexed { i, (_, v) ->
                if (v == null) { path?.let { drawPath(it, c.primary, style = Stroke(3.dp.toPx(), cap = StrokeCap.Round)) }; path = null; return@forEachIndexed }
                val x = if (n == 1) size.width / 2 else stepX * i
                val y = size.height - (v.coerceIn(0, maxValue) / maxValue.toFloat()) * size.height
                path = (path ?: Path().apply { moveTo(x, y) }).apply { lineTo(x, y) }
                drawCircle(c.primary, 4.dp.toPx(), Offset(x, y))
                drawCircle(c.surface, 2.dp.toPx(), Offset(x, y))
            }
            path?.let { drawPath(it, c.primary, style = Stroke(3.dp.toPx(), cap = StrokeCap.Round)) }
        }
        Spacer(Modifier.height(6.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            points.forEach { (m, _) -> Text(m, style = DfiTheme.typography.caption, color = c.onSurfaceVariant, maxLines = 1) }
        }
    }
}

@Composable
fun AvatarInitials(name: String?, size: Dp = 48.dp) {
    val initials = name?.trim()?.split(Regex("\\s+"))?.filter { it.isNotEmpty() }?.take(2)?.joinToString("") { it.first().uppercase() }.orEmpty().ifEmpty { "🙂" }
    Box(Modifier.size(size).clip(CircleShape).background(DfiTheme.colors.primarySoft), contentAlignment = Alignment.Center) {
        Text(initials, style = DfiTheme.typography.cardTitle, color = DfiTheme.colors.primaryDark)
    }
}

// ---------------------------------------------------------------- States

@Composable
fun EmptyState(
    title: String,
    body: String,
    modifier: Modifier = Modifier,
    imageKey: String? = null,
    fallback: FallbackArt = FallbackArt.LEAVES,
    action: String? = null,
    onAction: (() -> Unit)? = null
) {
    Column(modifier.fillMaxWidth().padding(vertical = 24.dp, horizontal = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        AppImage(imageKey, null, Modifier.size(140.dp).clip(DfiTheme.shapes.hero), fallback = fallback)
        Spacer(Modifier.height(16.dp))
        Text(title, style = DfiTheme.typography.cardTitle, color = DfiTheme.colors.onSurface, textAlign = TextAlign.Center)
        Spacer(Modifier.height(6.dp))
        Text(body, style = DfiTheme.typography.bodySmall, color = DfiTheme.colors.onSurfaceVariant, textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(max = 320.dp))
        if (action != null && onAction != null) { Spacer(Modifier.height(16.dp)); SecondaryButton(action, onAction) }
    }
}

@Composable
fun LoadingState(modifier: Modifier = Modifier, label: String = "Loading") {
    Box(modifier.fillMaxWidth().padding(32.dp).semantics { contentDescription = label }, contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = DfiTheme.colors.primary, strokeWidth = 3.dp, modifier = Modifier.size(32.dp))
    }
}

@Composable
fun ErrorState(message: String, modifier: Modifier = Modifier, onRetry: (() -> Unit)? = null) {
    DfiCard(modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.ErrorOutline, null, tint = DfiTheme.colors.error)
            Spacer(Modifier.width(10.dp))
            Text(message, style = DfiTheme.typography.bodySmall, color = DfiTheme.colors.onSurface, modifier = Modifier.weight(1f))
            if (onRetry != null) LinkButton("Retry", onRetry)
        }
    }
}

@Composable
fun InfoNote(text: String, modifier: Modifier = Modifier, icon: ImageVector? = null) {
    Row(modifier.fillMaxWidth().clip(DfiTheme.shapes.tile).background(DfiTheme.colors.surfaceMuted).padding(12.dp),
        verticalAlignment = Alignment.CenterVertically) {
        if (icon != null) { Icon(icon, null, tint = DfiTheme.colors.onSurfaceVariant, modifier = Modifier.size(18.dp)); Spacer(Modifier.width(8.dp)) }
        Text(text, style = DfiTheme.typography.caption, color = DfiTheme.colors.onSurfaceVariant)
    }
}

@Composable
fun ConfirmDialog(
    title: String,
    body: String,
    confirm: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    destructive: Boolean = false,
    confirmEnabled: Boolean = true,
    extra: (@Composable () -> Unit)? = null
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DfiTheme.colors.surface,
        shape = DfiTheme.shapes.hero,
        title = { Text(title, style = DfiTheme.typography.sectionTitle) },
        text = { Column { Text(body, style = DfiTheme.typography.bodySmall, color = DfiTheme.colors.onSurfaceVariant); extra?.let { Spacer(Modifier.height(12.dp)); it() } } },
        confirmButton = {
            TextButton(onClick = onConfirm, enabled = confirmEnabled) {
                Text(confirm, style = DfiTheme.typography.labelSmall, color = if (!confirmEnabled) DfiTheme.colors.disabled else if (destructive) DfiTheme.colors.error else DfiTheme.colors.primaryDark)
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel", style = DfiTheme.typography.labelSmall, color = DfiTheme.colors.onSurfaceVariant) } }
    )
}

// ---------------------------------------------------------------- Chrome

data class BottomItem(val label: String, val icon: ImageVector, val selectedIcon: ImageVector)

/** Docked bar: 64dp, hairline top border, 3×20dp indicator above the active item, no pill. */
@Composable
fun BottomBar(items: List<BottomItem>, selected: Int, onSelect: (Int) -> Unit) {
    val c = DfiTheme.colors
    Column(Modifier.fillMaxWidth().background(c.surface).navigationBarsPadding()) {
        Divider()
        Row(Modifier.fillMaxWidth().height(64.dp)) {
            items.forEachIndexed { i, item ->
                val sel = i == selected
                Column(
                    Modifier.weight(1f).fillMaxSize().selectable(sel, role = Role.Tab) { onSelect(i) },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(Modifier.width(20.dp).height(3.dp).clip(DfiTheme.shapes.pill).background(if (sel) c.primary else Color.Transparent))
                    Spacer(Modifier.height(9.dp))
                    Icon(if (sel) item.selectedIcon else item.icon, null, tint = if (sel) c.primaryDark else c.onSurfaceVariant, modifier = Modifier.size(24.dp))
                    Spacer(Modifier.height(2.dp))
                    Text(item.label, style = DfiTheme.typography.caption, color = if (sel) c.primaryDark else c.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
        }
    }
}

@Composable
fun OfflineBanner(visible: Boolean, text: String = "You're offline. Showing saved information.") {
    AnimatedVisibility(visible) {
        Row(Modifier.fillMaxWidth().background(DfiTheme.colors.surfaceMuted).padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.CloudOff, null, tint = DfiTheme.colors.onSurfaceVariant, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(8.dp))
            Text(text, style = DfiTheme.typography.caption, color = DfiTheme.colors.onSurfaceVariant)
        }
    }
}

/** Opens a neutral screen immediately (Help area). */
@Composable
fun QuickExitChip(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier.heightIn(min = 40.dp).clip(DfiTheme.shapes.pill).background(DfiTheme.colors.surface)
            .border(1.dp, DfiTheme.colors.outline, DfiTheme.shapes.pill)
            .clickable(onClickLabel = "Quick exit to a neutral screen", onClick = onClick).padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Rounded.VisibilityOff, null, tint = DfiTheme.colors.onSurfaceVariant, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(6.dp))
        Text("Quick exit", style = DfiTheme.typography.caption, color = DfiTheme.colors.onSurface)
    }
}
