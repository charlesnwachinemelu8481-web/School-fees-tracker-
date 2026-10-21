package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FeeCategoryItem
import com.example.ui.theme.BackgroundSlate
import com.example.ui.theme.BorderSlate
import com.example.ui.theme.DeepTeal
import com.example.ui.theme.DeepTealContainer
import com.example.ui.theme.OnDeepTealContainer
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardSubtle
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.UUID

@Composable
fun FeeStructureScreen(
    currentStructures: Map<String, List<FeeCategoryItem>>,
    onSaveStructure: (gradeClass: String, items: List<FeeCategoryItem>) -> Unit,
    modifier: Modifier = Modifier
) {
    val classOptions = listOf(
        "Grade 7A", "Grade 7B", "Grade 8A", "Grade 8B",
        "Grade 9A", "Grade 10 Sci", "Grade 11 Arts", "Grade 12 Sci"
    )
    var selectedClass by remember { mutableStateOf("Grade 10 Sci") }

    val terms = listOf("Term 1", "Term 2", "Term 3")
    var selectedTermIndex by remember { mutableStateOf(0) }

    // Editable draft items for selected class
    val itemsForClass = currentStructures[selectedClass] ?: listOf(
        FeeCategoryItem(UUID.randomUUID().toString(), "Tuition Fee", "Tuition", 950.0, "Term 1"),
        FeeCategoryItem(UUID.randomUUID().toString(), "Textbooks & Library", "Books", 160.0, "Term 1"),
        FeeCategoryItem(UUID.randomUUID().toString(), "School Uniform & Kit", "Uniform", 120.0, "Term 1"),
        FeeCategoryItem(UUID.randomUUID().toString(), "STEM & Science Lab", "Lab & Tech", 180.0, "Term 1"),
        FeeCategoryItem(UUID.randomUUID().toString(), "Athletics & Clubs", "Sports", 70.0, "Term 1"),
        FeeCategoryItem(UUID.randomUUID().toString(), "Campus Tech & ICT", "Development", 80.0, "Term 1")
    )

    var draftItems by remember(selectedClass) {
        mutableStateOf(itemsForClass)
    }

    var showAddItemDialog by remember { mutableStateOf(false) }
    var newItemName by remember { mutableStateOf("") }
    var newItemCategory by remember { mutableStateOf("Tuition") }
    var newItemAmount by remember { mutableStateOf("100.00") }

    val currentTotal = draftItems.sumOf { it.amount }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundSlate),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Column {
                Text(
                    text = "Fee Structure Setup",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Configure mandatory and optional fee components per class, term, and category",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }
        }

        // Class Selector Carousel
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSlate),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Select Academic Class Level:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(classOptions) { cls ->
                            FilterChip(
                                selected = selectedClass == cls,
                                onClick = { selectedClass = cls },
                                label = { Text(cls, fontSize = 13.sp, fontWeight = FontWeight.Medium) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = DeepTealContainer,
                                    selectedLabelColor = OnDeepTealContainer
                                )
                            )
                        }
                    }
                }
            }
        }

        // Term Tabs & Items Editor
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSlate),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "$selectedClass Fee Schedule",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Define fee amounts per category for ${terms[selectedTermIndex]}",
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        }

                        OutlinedButton(
                            onClick = { showAddItemDialog = !showAddItemDialog },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("add_fee_item_toggle")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Category", fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    TabRow(
                        selectedTabIndex = selectedTermIndex,
                        containerColor = SurfaceCardSubtle,
                        contentColor = DeepTeal
                    ) {
                        terms.forEachIndexed { idx, name ->
                            Tab(
                                selected = selectedTermIndex == idx,
                                onClick = { selectedTermIndex = idx },
                                text = { Text(name, fontSize = 13.sp, fontWeight = FontWeight.SemiBold) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Inline Add New Item row if toggled
                    if (showAddItemDialog) {
                        Surface(
                            color = BackgroundSlate,
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderSlate),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("Add Custom Category Component", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DeepTeal)
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedTextField(
                                        value = newItemName,
                                        onValueChange = { newItemName = it },
                                        label = { Text("Component Name") },
                                        singleLine = true,
                                        modifier = Modifier.weight(1.5f)
                                    )
                                    OutlinedTextField(
                                        value = newItemCategory,
                                        onValueChange = { newItemCategory = it },
                                        label = { Text("Category") },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f)
                                    )
                                    OutlinedTextField(
                                        value = newItemAmount,
                                        onValueChange = { newItemAmount = it },
                                        label = { Text("Amount ($)") },
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    OutlinedButton(onClick = { showAddItemDialog = false }) {
                                        Text("Cancel", fontSize = 12.sp)
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(
                                        onClick = {
                                            val amt = newItemAmount.toDoubleOrNull() ?: 50.0
                                            if (newItemName.isNotBlank()) {
                                                val newItem = FeeCategoryItem(
                                                    id = UUID.randomUUID().toString(),
                                                    name = newItemName,
                                                    category = newItemCategory,
                                                    amount = amt,
                                                    term = terms[selectedTermIndex]
                                                )
                                                draftItems = draftItems + newItem
                                                newItemName = ""
                                                showAddItemDialog = false
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = DeepTeal)
                                    ) {
                                        Text("Add to List", fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    // List of editable fee items
                    draftItems.forEachIndexed { index, item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1.8f)) {
                                Text(
                                    text = item.name,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Category: ${item.category}",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = if (item.amount % 1.0 == 0.0) item.amount.toInt().toString() else item.amount.toString(),
                                    onValueChange = { newVal ->
                                        val parsed = newVal.toDoubleOrNull() ?: 0.0
                                        draftItems = draftItems.mapIndexed { idx, itm ->
                                            if (idx == index) itm.copy(amount = parsed) else itm
                                        }
                                    },
                                    prefix = { Text("$", color = DeepTeal, fontWeight = FontWeight.Bold) },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    modifier = Modifier.width(130.dp)
                                )

                                IconButton(
                                    onClick = {
                                        draftItems = draftItems.filterIndexed { idx, _ -> idx != index }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Remove item",
                                        tint = TextMuted,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                        HorizontalDivider(color = BorderSlate.copy(alpha = 0.5f))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Summary Total Box
                    Surface(
                        color = SurfaceCardSubtle,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Total Term Fee for $selectedClass",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Approx. annual session fee: $${String.format("%,.2f", currentTotal * 3)}",
                                    fontSize = 12.sp,
                                    color = TextMuted
                                )
                            }

                            Text(
                                text = "$${String.format("%,.2f", currentTotal)}",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 22.sp,
                                color = DeepTeal
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Save Structure Button
                    Button(
                        onClick = {
                            onSaveStructure(selectedClass, draftItems)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DeepTeal),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("save_structure_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Save Structure for $selectedClass",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }
}
