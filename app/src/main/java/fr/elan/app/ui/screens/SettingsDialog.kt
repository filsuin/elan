package fr.elan.app.ui.screens

import android.os.Build
import android.app.TimePickerDialog
import androidx.compose.ui.platform.LocalContext
import java.util.Locale
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fr.elan.app.BuildConfig
import fr.elan.app.R
import fr.elan.app.domain.ThemeMode
import fr.elan.app.domain.UserSettings
import fr.elan.app.ui.components.dialogContentMaxHeight
import fr.elan.app.ui.components.dialogContentMaxHeight

@Composable
fun SettingsDialog(
    settings: UserSettings, busy: Boolean,
    onUpdate: (UserSettings) -> Unit, onDismiss: () -> Unit,
    onExport: () -> Unit, onImport: () -> Unit, onErase: () -> Unit,
    notificationsAllowed: Boolean = false,
    onReminder: (Boolean) -> Unit = {}, onReminderTime: (Int, Int) -> Unit = { _, _ -> }, onNotificationSettings: () -> Unit = {},
) {
    val context = LocalContext.current
    var name by rememberSaveable { mutableStateOf(settings.firstName) }
    var confirmErase by rememberSaveable { mutableStateOf(false) }
    var confirmExport by rememberSaveable { mutableStateOf(false) }
    AlertDialog(onDismissRequest = onDismiss,
        title = { Text("Mon espace") },
        text = {
            Column(Modifier.heightIn(max = dialogContentMaxHeight(560.dp)).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it.take(30) }, label = { Text("Mon prénom (facultatif)") },
                    singleLine = true, modifier = Modifier.fillMaxWidth())
                TextButton(onClick = { onUpdate(settings.copy(firstName = name)) }, enabled = name.trim() != settings.firstName) {
                    Text("Enregistrer mon prénom")
                }
                Text("Apparence", style = MaterialTheme.typography.titleMedium)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(ThemeMode.SYSTEM to "Système", ThemeMode.LIGHT to "Clair", ThemeMode.DARK to "Sombre").forEach { (mode, label) ->
                        FilterChip(selected = settings.theme == mode, onClick = { onUpdate(settings.copy(theme = mode)) }, label = { Text(label) })
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(Modifier.weight(1f)) {
                        Text("Couleurs du téléphone", style = MaterialTheme.typography.titleSmall)
                        Text("Material You · Android 12 et plus", style = MaterialTheme.typography.bodySmall)
                    }
                    Switch(checked = settings.dynamicColors, onCheckedChange = { onUpdate(settings.copy(dynamicColors = it)) },
                        enabled = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                }
                HorizontalDivider()
                Text("Mon rappel quotidien", style = MaterialTheme.typography.titleMedium)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Une invitation facultative", Modifier.weight(1f))
                    Switch(settings.reminderEnabled, onReminder, enabled = !busy)
                }
                OutlinedButton(onClick = {
                    TimePickerDialog(context, { _, hour, minute -> onReminderTime(hour, minute) },
                        settings.reminderHour, settings.reminderMinute, true).show()
                }, enabled = !busy) { Text("Vers " + String.format(Locale.ROOT, "%02d:%02d", settings.reminderHour, settings.reminderMinute)) }
                Text("Heure indicative : Android peut retarder la notification pour économiser la batterie. Le rappel suit l'heure locale. Aucun rappel après un arrêt forcé avant de rouvrir l'app.", style = MaterialTheme.typography.bodySmall)
                if (!notificationsAllowed) {
                    Text("Notifications non autorisées par Android. Le rappel ne pourra pas s'afficher.", style = MaterialTheme.typography.bodySmall)
                    TextButton(onClick = onNotificationSettings) { Text("Ouvrir les réglages de notification") }
                }
                HorizontalDivider()
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Lock, null); Text("Mes données", style = MaterialTheme.typography.titleMedium)
                }
                Text("Aucun compte, aucune publicité, aucun suivi. Le journal reste dans l'espace privé de l'application. Il n'est pas chiffré par un mot de passe propre à Élan.")
                Text("La sauvegarde automatique Android est désactivée. Pense à exporter avant de désinstaller l'application ou de changer de téléphone.")
                OutlinedButton(onClick = { confirmExport = true }, enabled = !busy, modifier = Modifier.fillMaxWidth()) { Text("Exporter ma sauvegarde JSON") }
                OutlinedButton(onClick = onImport, enabled = !busy, modifier = Modifier.fillMaxWidth()) { Text("Restaurer une sauvegarde") }
                Text("L'import remplace les données actuelles après confirmation. Formats Élan v1/v2, 10 Mo maximum. Export en v2, incluant favoris, séances, historique et réglages. Le rappel est désactivé après restauration.", style = MaterialTheme.typography.bodySmall)
                if (busy) LinearProgressIndicator(Modifier.fillMaxWidth())
                TextButton(onClick = { confirmErase = true }, enabled = !busy) {
                    Text("Effacer toutes mes données", color = MaterialTheme.colorScheme.error)
                }
                HorizontalDivider()
                Text(stringResource(R.string.app_version, BuildConfig.VERSION_NAME), style = MaterialTheme.typography.labelLarge)
                Text("Une base d'application de bien-être, pas un dispositif médical. Les exercices sont des suggestions facultatives. Le yoga doit être adapté à tes possibilités ; un accompagnement qualifié est préférable pour débuter.", style = MaterialTheme.typography.bodySmall)
                Text("Repères éditoriaux : NHS, Raising low self-esteem ; NCCIH, Yoga: Effectiveness and Safety. Textes et schémas originaux, non validés cliniquement.", style = MaterialTheme.typography.bodySmall)
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Fermer") } },
    )
    if (confirmExport) {
        AlertDialog(onDismissRequest = { confirmExport = false }, title = { Text("Exporter des données personnelles") },
            text = { Text("Le fichier contiendra ton prénom, tes notes, humeurs, routines, victoires, favoris, séances, historique et réglages en clair. Choisis un emplacement de confiance. Un fournisseur cloud sélectionné dans le sélecteur Android peut synchroniser ce fichier.") },
            confirmButton = { TextButton(onClick = { confirmExport = false; onExport() }) { Text("Choisir un emplacement") } },
            dismissButton = { TextButton(onClick = { confirmExport = false }) { Text("Annuler") } })
    }
    if (confirmErase) {
        AlertDialog(onDismissRequest = { confirmErase = false }, title = { Text("Tout effacer sur ce téléphone ?") },
            text = { Text("Cette action est irréversible : les humeurs, notes, victoires, favoris, séances, pratiques et réglages seront supprimés. Le rappel sera annulé. Les quatre routines de départ seront rétablies. Les fichiers déjà exportés ne seront pas effacés.") },
            confirmButton = { TextButton(onClick = { confirmErase = false; onErase(); onDismiss() }) { Text("Tout effacer") } },
            dismissButton = { TextButton(onClick = { confirmErase = false }) { Text("Annuler") } })
    }
}
