package com.example.weatherapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.weatherapp.location.LocationSearchResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationSearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    searchResults: List<LocationSearchResult>,
    onLocationSelected: (LocationSearchResult) -> Unit,
    onCurrentLocationClick: () -> Unit,
    onSettingsClick: () -> Unit,
    isSearching: Boolean,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Search bar
            DockedSearchBar(
                query = searchQuery,
                onQueryChange = { 
                    onSearchQueryChange(it)
                    expanded = it.isNotEmpty()
                },
                onSearch = { expanded = false },
                active = expanded,
                onActiveChange = { expanded = it },
                placeholder = { Text("Vyhledat místo...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Vyhledat"
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { 
                            onSearchQueryChange("")
                            expanded = false
                        }) {
                            Icon(
                                imageVector = Icons.Outlined.Close,
                                contentDescription = "Vymazat"
                            )
                        }
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                if (isSearching) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    }
                } else if (searchResults.isNotEmpty()) {
                    searchResults.forEach { result ->
                        ListItem(
                            headlineContent = { Text(result.displayName) },
                            modifier = Modifier.clickable {
                                onLocationSelected(result)
                                expanded = false
                            }
                        )
                        HorizontalDivider()
                    }
                } else if (searchQuery.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Žádné výsledky",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            // Tlačítko pro aktuální polohu
            FilledIconButton(
                onClick = onCurrentLocationClick,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MyLocation,
                    contentDescription = "Aktuální poloha"
                )
            }
            
            // Tlačítko nastavení
            FilledTonalIconButton(
                onClick = onSettingsClick,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Nastavení"
                )
            }
        }
    }
}
