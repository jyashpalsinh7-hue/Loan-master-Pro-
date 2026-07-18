import sys

path = "app/src/main/java/com/loanmaster/pro/core/responsive/Responsive.kt"
with open(path, "r") as f:
    content = f.read()

target = """@Composable
fun AdaptiveRowCol(
    modifier: Modifier = Modifier,
    columns: Int = LoanMasterTheme.grids.inputColumns,
    content1: @Composable (Modifier) -> Unit,
    content2: @Composable (Modifier) -> Unit
) {
    BoxWithConstraints(modifier = modifier) {
        // Below this local width, two OutlinedTextFields with leading icons and
        // floating labels don't have enough room to show full label text without
        // clipping (e.g. "Monthly Income", "Home Loan"). At or above it, keep the
        // existing side-by-side layout exactly as before (this is what already
        // works correctly on wider phones like Redmi 13 at ~412dp).
        val minWidthForTwoColumns = 356.dp
        val effectiveColumns = if (maxWidth >= minWidthForTwoColumns) 2 else 1

        // TEMPORARY DIAGNOSTIC — remove after reading the value on-device
        androidx.compose.material3.Text(
            text = "DEBUG maxWidth=$maxWidth cols=$columns effCols=$effectiveColumns",
            color = androidx.compose.ui.graphics.Color.Red,
            fontSize = androidx.compose.ui.unit.TextUnit(10f, androidx.compose.ui.unit.TextUnitType.Sp),
            modifier = Modifier
                .align(androidx.compose.ui.Alignment.TopStart)
                .background(androidx.compose.ui.graphics.Color.Yellow)
                .zIndex(10f)
        )

        if (effectiveColumns == 1) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.md)
            ) {
                content1(Modifier.fillMaxWidth())
                content2(Modifier.fillMaxWidth())
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.gridGutter)
            ) {
                content1(Modifier.weight(1f))
                content2(Modifier.weight(1f))
            }
        }
    }
}"""

replacement = """@Composable
fun AdaptiveRowCol(
    modifier: Modifier = Modifier,
    columns: Int = LoanMasterTheme.grids.inputColumns,
    content1: @Composable (Modifier) -> Unit,
    content2: @Composable (Modifier) -> Unit
) {
    BoxWithConstraints(modifier = modifier) {
        // Calculate the actual minimum width one field needs to render without
        // clipping, based on real design tokens rather than a guessed constant.
        // This makes the 2-column decision correct regardless of how much padding
        // a given screen applies upstream before reaching this composable.
        //
        // Components summed:
        //   - leading icon width (LoanMasterTheme.components.iconSmall)
        //   - spacing between icon and label text (LoanMasterTheme.spacing.sm)
        //   - estimated rendered width of the longest known field label
        //     ("Monthly Income", 15 characters), approximated at roughly 0.55x
        //     the font size per character for this font family/weight — a
        //     reasonable estimate for Latin proportional text, not a precise
        //     text measurement, but far more grounded than an arbitrary dp guess
        //   - internal OutlinedTextField horizontal content padding (~24dp,
        //     Material 3's default start+end content inset)
        //   - a small safety buffer so text isn't touching the field's edge
        val density = LocalDensity.current
        val longestLabelCharCount = 15 // "Monthly Income"
        val estimatedCharWidth = with(density) {
            (LoanMasterTheme.typography.body.fontSize.toPx() * 0.55f).toDp()
        }
        val estimatedLabelWidth = estimatedCharWidth * longestLabelCharCount
        val fieldInternalPadding = 24.dp
        val safetyBuffer = 12.dp

        val minWidthPerField = LoanMasterTheme.components.iconSmall +
            LoanMasterTheme.spacing.sm +
            estimatedLabelWidth +
            fieldInternalPadding +
            safetyBuffer

        // Two fields side by side also need the gutter between them, plus this
        // composable's own horizontal space is shared 50/50 by weight(1f), so
        // the true minimum LOCAL width needed for 2-column is roughly double
        // one field's minimum, plus the gutter.
        val minWidthForTwoColumns = (minWidthPerField * 2) + LoanMasterTheme.spacing.gridGutter

        val effectiveColumns = if (maxWidth >= minWidthForTwoColumns) 2 else 1

        if (effectiveColumns == 1) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.md)
            ) {
                content1(Modifier.fillMaxWidth())
                content2(Modifier.fillMaxWidth())
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.gridGutter)
            ) {
                content1(Modifier.weight(1f))
                content2(Modifier.weight(1f))
            }
        }
    }
}"""

if target in content:
    content = content.replace(target, replacement)
    print("Replaced target")
else:
    print("Target not found")

with open(path, "w") as f:
    f.write(content)
