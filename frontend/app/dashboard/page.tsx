'use client';

import { useState } from 'react';
import { Button } from '@/components/ui/button';
import { OverviewCards } from '@/components/dashboard/overview-cards';
import { EventsTimeline } from '@/components/dashboard/events-timeline';
import { StatusChart } from '@/components/dashboard/status-chart';
import { AlertsPanel } from '@/components/dashboard/alerts-panel';
import { MaintenanceTimeline } from '@/components/dashboard/maintenance-timeline';
import {
  useDashboardStats,
  useEquipmentStatusDistribution,
  useActiveAlerts,
  useUpcomingMaintenance,
} from '@/hooks/use-dashboard';
import { useWebSocket } from '@/hooks/use-websocket';
import {
  exportDashboardSummary,
  exportEvents,
  exportAlerts,
  exportToPDF,
} from '@/lib/export-utils';
import {
  Activity,
  AlertTriangle,
  Users,
  Droplet,
  Zap,
  Download,
  FileText,
  RefreshCw,
} from 'lucide-react';

export default function DashboardPage() {
  const [refreshKey, setRefreshKey] = useState(0);

  // Real-time WebSocket connection
  const { isConnected } = useWebSocket({ enabled: true });

  // Dashboard data hooks
  const stats = useDashboardStats();
  const { statusData, equipmentTypeData } = useEquipmentStatusDistribution();
  const alerts = useActiveAlerts();
  const { data: maintenanceItems, isLoading: maintenanceLoading } = useUpcomingMaintenance();

  // Overview statistics
  const overviewStats = [
    {
      name: 'Total Farmers',
      value: stats.totalFarmers,
      icon: Users,
      color: 'text-blue-600',
      bgColor: 'bg-blue-100',
      change: '+12%',
      changeType: 'increase' as const,
      description: 'Registered farmers',
    },
    {
      name: 'Total Equipment',
      value: stats.totalEquipment,
      icon: Activity,
      color: 'text-green-600',
      bgColor: 'bg-green-100',
      change: '+8%',
      changeType: 'increase' as const,
      description: `${stats.totalPumps} pumps, ${stats.totalSensors} sensors`,
    },
    {
      name: 'Active Equipment',
      value: stats.activeEquipment,
      icon: Zap,
      color: 'text-primary-600',
      bgColor: 'bg-primary-100',
      change: '+5%',
      changeType: 'increase' as const,
      description: 'Currently operational',
    },
    {
      name: 'Active Alerts',
      value: alerts.length,
      icon: AlertTriangle,
      color: 'text-red-600',
      bgColor: 'bg-red-100',
      change: alerts.length > 0 ? `${alerts.filter(a => a.severity === 'high').length} critical` : 'All clear',
      changeType: alerts.length > 10 ? 'increase' as const : 'decrease' as const,
      description: 'Requires attention',
    },
  ];

  const handleRefresh = () => {
    setRefreshKey(prev => prev + 1);
    window.location.reload();
  };

  return (
    <div className="space-y-6 dashboard-content">
      {/* Header with Actions */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold">Dashboard</h1>
          <p className="mt-2 text-muted-foreground">
            Welcome back! Here's an overview of your equipment monitoring system.
          </p>
        </div>
        <div className="flex items-center gap-2 no-print">
          {/* Real-time indicator */}
          <div className="flex items-center gap-2 px-3 py-2 rounded-lg border">
            <div className={`w-2 h-2 rounded-full ${isConnected ? 'bg-green-500' : 'bg-gray-400'} animate-pulse`}></div>
            <span className="text-sm">{isConnected ? 'Live' : 'Offline'}</span>
          </div>
          
          {/* Export actions */}
          <Button variant="outline" size="sm" onClick={handleRefresh}>
            <RefreshCw className="w-4 h-4 mr-2" />
            Refresh
          </Button>
          <Button variant="outline" size="sm" onClick={() => exportDashboardSummary(stats)}>
            <Download className="w-4 h-4 mr-2" />
            Export CSV
          </Button>
          <Button variant="outline" size="sm" onClick={exportToPDF}>
            <FileText className="w-4 h-4 mr-2" />
            Export PDF
          </Button>
        </div>
      </div>

      {/* Overview Cards */}
      <OverviewCards stats={overviewStats} />

      {/* Main Grid Layout */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Left Column - 2/3 width */}
        <div className="lg:col-span-2 space-y-6">
          {/* Events Timeline */}
          <EventsTimeline events={stats.recentEvents} showFilters={true} />

          {/* Equipment Status Charts */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <StatusChart
              data={equipmentTypeData}
              type="pie"
              title="Equipment Distribution"
              description="By equipment type"
            />
            <StatusChart
              data={statusData.map(item => ({
                name: item.name,
                value: item.pumps + item.sensors,
              }))}
              type="bar"
              title="Status Distribution"
              description="Active vs inactive equipment"
            />
          </div>
        </div>

        {/* Right Column - 1/3 width */}
        <div className="space-y-6">
          {/* Active Alerts Panel */}
          <AlertsPanel alerts={alerts} />

          {/* Maintenance Timeline */}
          <MaintenanceTimeline items={maintenanceItems || []} isLoading={maintenanceLoading} />
        </div>
      </div>

      {/* Quick Stats Summary */}
      <div className="grid grid-cols-2 md:grid-cols-4 lg:grid-cols-6 gap-4">
        <div className="p-4 border rounded-lg">
          <p className="text-xs text-muted-foreground">Pumps</p>
          <p className="text-2xl font-bold">{stats.totalPumps}</p>
        </div>
        <div className="p-4 border rounded-lg">
          <p className="text-xs text-muted-foreground">Sensors</p>
          <p className="text-2xl font-bold">{stats.totalSensors}</p>
        </div>
        <div className="p-4 border rounded-lg">
          <p className="text-xs text-muted-foreground">Low Battery</p>
          <p className="text-2xl font-bold text-yellow-600">{stats.lowBatterySensors}</p>
        </div>
        <div className="p-4 border rounded-lg">
          <p className="text-xs text-muted-foreground">Critical</p>
          <p className="text-2xl font-bold text-red-600">{stats.criticalSensors}</p>
        </div>
        <div className="p-4 border rounded-lg">
          <p className="text-xs text-muted-foreground">Offline</p>
          <p className="text-2xl font-bold text-gray-600">{stats.offlineSensors}</p>
        </div>
        <div className="p-4 border rounded-lg">
          <p className="text-xs text-muted-foreground">Maintenance</p>
          <p className="text-2xl font-bold text-orange-600">{stats.maintenancePumps}</p>
        </div>
      </div>
    </div>
  );
}
