package mx.edu.utez.musicp.ui.components

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope.weight
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import mx.edu.utez.musicp.R
import mx.edu.utez.musicp.data.model.Playlist

@Composable
fun PlaylistCard(
    p: Playlist,
    onCardClick: (Int) -> Unit,
    onEditClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {

    Card(
        modifier = modifier
            .fillMaxWidth()     // Ahora es más ancha
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onCardClick(p.id) },
        elevation = CardDefaults.cardElevation(4.dp),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),   //  Más redondeada
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface   //  Color claro
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Log.d("COIL_DEBUG", "Intentando cargar: ${p.imagenUri}")
            //  Imagen más grande
            AsyncImage(
                model = p.imagenUri,
                contentDescription = "Imagen de ${p.titulo}",
                placeholder = painterResource(id = R.drawable.noposter),
                error = painterResource(id = R.drawable.noposter),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(width = 90.dp, height = 135.dp) // Más grande
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(14.dp))
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Título
                Text(
                    text = p.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))


                //  Más espacio antes de la sinopsis
                Spacer(modifier = Modifier.height(12.dp))

                //  Sinopsis gris suave
                Text(
                    text = p.descripcion,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    maxLines = 7,
                    overflow = TextOverflow.Ellipsis
                )
            }

            IconButton(
                onClick = { onEditClick(p.id) } // 💡 Dispara la navegación a la edición
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Editar Playlist",
                    tint = MaterialTheme.colorScheme.primary
                )
            }


        }
    }
}
