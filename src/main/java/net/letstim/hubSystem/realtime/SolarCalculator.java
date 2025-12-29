package net.letstim.hubSystem.realtime;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class SolarCalculator {

    public static SunTimes calculateSunTimes(ZoneId timezone) {
        ZonedDateTime now = ZonedDateTime.now(timezone);
        LocalDate date = now.toLocalDate();

        int dayOfYear = date.getDayOfYear();
        double declination = 23.45 * Math.sin(Math.toRadians((360.0 / 365.0) * (dayOfYear - 81)));
        double latitude = getLatitudeForTimezone(timezone);
        double hourAngle = calculateHourAngle(latitude, declination);

        double sunriseDecimal = 12.0 - hourAngle;
        double sunsetDecimal = 12.0 + hourAngle;

        sunriseDecimal = Math.max(4.0, Math.min(8.5, sunriseDecimal));
        sunsetDecimal = Math.max(15.5, Math.min(21.0, sunsetDecimal));

        int sunriseHour = (int) sunriseDecimal;
        int sunriseMinute = (int) ((sunriseDecimal - sunriseHour) * 60);
        int sunsetHour = (int) sunsetDecimal;
        int sunsetMinute = (int) ((sunsetDecimal - sunsetHour) * 60);

        LocalTime sunrise = LocalTime.of(sunriseHour, sunriseMinute);
        LocalTime sunset = LocalTime.of(sunsetHour, sunsetMinute);

        return new SunTimes(sunrise, sunset);
    }

    private static double calculateHourAngle(double latitude, double declination) {
        double latRad = Math.toRadians(latitude);
        double decRad = Math.toRadians(declination);
        double cosHourAngle = -Math.tan(latRad) * Math.tan(decRad);
        cosHourAngle = Math.max(-1, Math.min(1, cosHourAngle));
        double hourAngleRad = Math.acos(cosHourAngle);
        return Math.toDegrees(hourAngleRad) / 15.0;
    }

    private static double getLatitudeForTimezone(ZoneId timezone) {
        String zoneId = timezone.getId();
        String region = zoneId.contains("/") ? zoneId.split("/")[0] : "";

        return switch (region) {
            case "Europe" -> 50.0;
            case "America" -> {
                if (zoneId.contains("New_York") || zoneId.contains("Eastern")) yield 40.0;
                if (zoneId.contains("Los_Angeles") || zoneId.contains("Pacific")) yield 34.0;
                yield 40.0;
            }
            case "Asia" -> 35.0;
            case "Australia" -> -33.0;
            case "Africa" -> 0.0;
            default -> 50.0;
        };
    }

    public static class SunTimes {
        private final LocalTime sunrise;
        private final LocalTime sunset;

        public SunTimes(LocalTime sunrise, LocalTime sunset) {
            this.sunrise = sunrise;
            this.sunset = sunset;
        }

        public LocalTime getSunrise() {
            return sunrise;
        }

        public LocalTime getSunset() {
            return sunset;
        }

        public int getSunriseHour() {
            return sunrise.getHour();
        }

        public int getSunsetHour() {
            return sunset.getHour();
        }

        public double getSunriseDecimal() {
            return sunrise.getHour() + (sunrise.getMinute() / 60.0);
        }

        public double getSunsetDecimal() {
            return sunset.getHour() + (sunset.getMinute() / 60.0);
        }
    }
}
