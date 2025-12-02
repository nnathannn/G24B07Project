package com.example.smartair;
public class ProviderReportData {

    // Meta
    public String childName;
    public String providerName;
    public String startDate; // full report window
    public String endDate;

    // Access flags (from provider-users/{providerUid}/access/{childId})
    public boolean canSeeRescue;
    public boolean canSeeController;
    public boolean canSeeSymptoms;
    public boolean canSeeZones;      // from pef/summary
    public boolean canSeeTriages;
    public boolean canSeeSummaryCharts; // your "summary" toggle

    // 1) Rescue frequency
    public int rescueEventCount;

    // 2) Controller adherence summary
    public String controllerScheduleStart; // e.g. "2025-11-01"
    public String controllerScheduleEnd;   // e.g. "2025-12-01"
    public double controllerAdherencePercent; // 0–100

    // 3) Symptom burden
    public int problemSymptomDays;  // days with any problem symptom
    public java.util.List<CategoryCount> symptomCounts; // table + bar chart

    // 4) Zone distribution over time
    public java.util.List<ZoneDistributionRow> zoneTable; // table rows
    public java.util.List<DailyZonePoint> zoneTimeSeries; // data for time-series chart

    // 5) Notable triage incidents
    public java.util.List<TriageIncident> triageIncidents;




    public static class CategoryCount {
        public String label; // e.g. "Cough", "Wheeze"
        public int count;
    }

    // One row of the zone distribution table
    public static class ZoneDistributionRow {
        public String label;    // e.g. "2025-11-01" or "Nov 2025"
        public int greenCount;
        public int yellowCount;
        public int redCount;
    }

    // For time-series chart: index by date
    public static class DailyZonePoint {
        public String date; // "2025-11-01"
        public double greenPercent;  // 0–100
        public double yellowPercent;
        public double redPercent;
    }

    public static class TriageIncident {
        public String date;      // "2025-11-15"
        public String summary;   // "ED visit for severe symptoms"
    }
}
