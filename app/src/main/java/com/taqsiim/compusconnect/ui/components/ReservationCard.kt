package com.taqsiim.compusconnect.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.taqsiim.compusconnect.data.model.Reservation
import com.taqsiim.compusconnect.data.model.ReservationType
import androidx.compose.ui.tooling.preview.Preview
import com.taqsiim.compusconnect.ui.theme.CampusAppTheme

@Composable
fun ReservationCard(
    reservation: Reservation,
    modifier: Modifier = Modifier.width(280.dp)
) {
    val borderColor = when (reservation.type) {
        ReservationType.STUDY_ROOM -> Color(0xFF2196F3)
        ReservationType.SPORT -> Color(0xFF4CAF50)
        ReservationType.EVENT -> Color(0xFF9C27B0)
        ReservationType.SESSION -> Color(0xFFFF9800)
    }

    Card(
        modifier = modifier
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(2.dp, borderColor),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Status Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = when (reservation.type) {
                        ReservationType.STUDY_ROOM -> Color(0xFF2196F3)
                        ReservationType.SPORT -> Color(0xFF4CAF50)
                        else -> MaterialTheme.colorScheme.primary
                    }
                ) {
                    Text(
                        text = reservation.type.name.replace("_", " "),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(Color(0xFF4CAF50), CircleShape)
                    )
                    Text(
                        text = "Active",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF4CAF50),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Title
            Text(
                text = reservation.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Date and Time
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
//                Icon(
//                    imageVector = Icons.Default.CalendarToday,
//                    contentDescription = null,
//                    modifier = Modifier.size(16.dp),
//                    tint = MaterialTheme.colorScheme.onSurfaceVariant
//                )
                Text(
                    text = "Today", // TODO: Format reservation.startTime
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
//                Icon(
//                    imageVector = Icons.Default.Schedule,
//                    contentDescription = null,
//                    modifier = Modifier.size(16.dp),
//                    tint = MaterialTheme.colorScheme.onSurfaceVariant
//                )
                Text(
                    text = "2:00 PM - 4:00 PM", // TODO: Format times
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Reservation Card - Light")
@Composable
fun ReservationCardPreviewLight() {
    CampusAppTheme(darkTheme = false) {
        ReservationCard(
            reservation = Reservation(
                reservationId = "1",
                title = "Study Room A",
                startTime = "2023-10-20T10:00:00Z",
                endTime = "2023-10-20T12:00:00Z",
                type = ReservationType.STUDY_ROOM
            )
        )
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES, name = "Reservation Card - Dark")
@Composable
fun ReservationCardPreviewDark() {
    CampusAppTheme(darkTheme = true) {
        ReservationCard(
            reservation = Reservation(
                reservationId = "2",
                title = "Basketball Court",
                startTime = "2023-10-21T16:00:00Z",
                endTime = "2023-10-21T17:30:00Z",
                type = ReservationType.SPORT
            )
        )
    }
}
