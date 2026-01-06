/**
 * Export utilities for dashboard data
 */

/**
 * Export data to CSV format
 */
export function exportToCSV(data: any[], filename: string) {
  if (!data || data.length === 0) {
    console.error('No data to export');
    return;
  }

  // Get headers from first object
  const headers = Object.keys(data[0]);
  
  // Create CSV content
  const csvContent = [
    headers.join(','), // Header row
    ...data.map(row => 
      headers.map(header => {
        const value = row[header];
        // Handle values that might contain commas or quotes
        if (typeof value === 'string' && (value.includes(',') || value.includes('"'))) {
          return `"${value.replace(/"/g, '""')}"`;
        }
        return value;
      }).join(',')
    )
  ].join('\n');

  // Create blob and download
  const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
  const link = document.createElement('a');
  const url = URL.createObjectURL(blob);
  
  link.setAttribute('href', url);
  link.setAttribute('download', `${filename}_${new Date().toISOString().split('T')[0]}.csv`);
  link.style.visibility = 'hidden';
  
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
}

/**
 * Export dashboard summary to CSV
 */
export function exportDashboardSummary(stats: any) {
  const summaryData = [
    { metric: 'Total Farmers', value: stats.totalFarmers },
    { metric: 'Total Equipment', value: stats.totalEquipment },
    { metric: 'Total Pumps', value: stats.totalPumps },
    { metric: 'Total Sensors', value: stats.totalSensors },
    { metric: 'Active Equipment', value: stats.activeEquipment },
    { metric: 'Low Battery Sensors', value: stats.lowBatterySensors },
    { metric: 'Critical Sensors', value: stats.criticalSensors },
    { metric: 'Offline Sensors', value: stats.offlineSensors },
    { metric: 'Equipment in Maintenance', value: stats.maintenancePumps },
    { metric: 'Overdue Maintenance', value: stats.overduePumps },
    { metric: 'Critical Events', value: stats.criticalEvents },
    { metric: 'Warning Events', value: stats.warningEvents },
    { metric: 'Unacknowledged Events', value: stats.unacknowledgedEvents },
  ];

  exportToCSV(summaryData, 'dashboard_summary');
}

/**
 * Export events to CSV
 */
export function exportEvents(events: any[]) {
  const eventsData = events.map(event => ({
    id: event.id,
    message: event.message,
    equipmentId: event.equipmentId,
    equipmentType: event.equipmentType || 'N/A',
    severity: event.severity,
    type: event.type,
    timestamp: new Date(event.timestamp).toLocaleString(),
    acknowledged: event.acknowledged ? 'Yes' : 'No',
  }));

  exportToCSV(eventsData, 'events_report');
}

/**
 * Export alerts to CSV
 */
export function exportAlerts(alerts: any[]) {
  const alertsData = alerts.map(alert => ({
    id: alert.id,
    type: alert.type,
    equipmentId: alert.equipmentId,
    equipmentType: alert.equipmentType,
    message: alert.message,
    severity: alert.severity,
    timestamp: new Date(alert.timestamp).toLocaleString(),
  }));

  exportToCSV(alertsData, 'alerts_report');
}

/**
 * Print dashboard report
 */
export function printDashboard() {
  window.print();
}

/**
 * Generate PDF (using browser print to PDF)
 */
export function exportToPDF() {
  // Add print-specific styles
  const style = document.createElement('style');
  style.innerHTML = `
    @media print {
      body * {
        visibility: hidden;
      }
      .dashboard-content, .dashboard-content * {
        visibility: visible;
      }
      .dashboard-content {
        position: absolute;
        left: 0;
        top: 0;
        width: 100%;
      }
      .no-print {
        display: none !important;
      }
    }
  `;
  document.head.appendChild(style);
  
  // Trigger print
  window.print();
  
  // Remove style after print
  setTimeout(() => {
    document.head.removeChild(style);
  }, 1000);
}
