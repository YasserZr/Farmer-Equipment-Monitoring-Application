'use client';

import { useState } from 'react';
import { useEquipmentStatistics, usePumps, useSensors } from '@/hooks/use-equipment';
import { Button } from '@/components/ui/button';
import { KPICard } from '@/components/charts/kpi-card';
import { BarChartCard } from '@/components/charts/bar-chart-card';
import { AreaChartCard } from '@/components/charts/area-chart-card';
import { DonutChartCard } from '@/components/charts/donut-chart-card';
import { Download, FileText, Activity, AlertTriangle, Battery, Wrench } from 'lucide-react';

export default function ReportsPage() {
  const { data: statistics, isLoading: statsLoading } = useEquipmentStatistics();
  const { data: pumpsData } = usePumps(0, 100);
  const { data: sensorsData } = useSensors(0, 100);

  const pumps = pumpsData?.content || [];
  const sensors = sensorsData?.content || [];

  // Equipment Distribution - Using API statistics
  const equipmentDistribution = [
    { name: 'Pumps', value: statistics?.totalPumps || 0, color: '#3b82f6' },
    { name: 'Sensors', value: statistics?.totalSensors || 0, color: '#10b981' },
  ];

  // Status Distribution - Using API statistics
  const statusDistribution = [
    {
      name: 'Active',
      pumps: statistics?.activePumps || 0,
      sensors: statistics?.activeSensors || 0,
    },
    {
      name: 'Inactive',
      pumps: statistics?.inactivePumps || 0,
      sensors: statistics?.inactiveSensors || 0,
    },
    {
      name: 'Maintenance',
      pumps: statistics?.maintenancePumps || 0,
      sensors: 0,
    },
  ];

  // Battery Status Distribution - Using API statistics
  const batteryDistribution = [
    { 
      name: 'Good (>50%)', 
      value: (statistics?.totalSensors || 0) - (statistics?.lowBatterySensors || 0) - (statistics?.criticalBatterySensors || 0),
      color: '#10b981'
    },
    { name: 'Low (20-50%)', value: statistics?.lowBatterySensors || 0, color: '#f59e0b' },
    { name: 'Critical (<20%)', value: statistics?.criticalBatterySensors || 0, color: '#ef4444' },
  ];

  // Equipment activity over time (mock data - replace with actual API data when available)
  const activityTrends = [
    { name: 'Jan', active: statistics?.activeEquipment || 0, total: statistics?.totalEquipment || 0 },
    { name: 'Feb', active: Math.round((statistics?.activeEquipment || 0) * 0.95), total: statistics?.totalEquipment || 0 },
    { name: 'Mar', active: Math.round((statistics?.activeEquipment || 0) * 0.98), total: statistics?.totalEquipment || 0 },
    { name: 'Apr', active: Math.round((statistics?.activeEquipment || 0) * 1.02), total: (statistics?.totalEquipment || 0) + 1 },
    { name: 'May', active: Math.round((statistics?.activeEquipment || 0) * 1.01), total: (statistics?.totalEquipment || 0) + 1 },
    { name: 'Jun', active: statistics?.activeEquipment || 0, total: statistics?.totalEquipment || 0 },
  ];

  // Summary metrics - Using API statistics
  const totalEquipment = statistics?.totalEquipment || 0;
  const activeEquipment = statistics?.activeEquipment || 0;
  const maintenanceScheduled = statistics?.maintenancePumps || 0;
  const lowBatterySensors = (statistics?.lowBatterySensors || 0) + (statistics?.criticalBatterySensors || 0);

  return (
    <div className="space-y-6 max-w-7xl">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold">Equipment Reports</h1>
          <p className="text-muted-foreground mt-1">
            Analytics and insights for equipment monitoring
          </p>
        </div>
        <div className="flex items-center gap-2">
          <Button variant="outline">
            <FileText className="w-4 h-4 mr-2" />
            Export PDF
          </Button>
          <Button variant="outline">
            <Download className="w-4 h-4 mr-2" />
            Export CSV
          </Button>
        </div>
      </div>

      {/* Summary Cards */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <KPICard
          title="Total Equipment"
          value={totalEquipment}
          description="All equipment in inventory"
          icon={Activity}
          iconColor="text-blue-600"
          iconBgColor="bg-blue-100"
          isLoading={statsLoading}
        />

        <KPICard
          title="Active Equipment"
          value={activeEquipment}
          description={`${totalEquipment > 0 ? Math.round((activeEquipment / totalEquipment) * 100) : 0}% of total`}
          icon={Activity}
          iconColor="text-green-600"
          iconBgColor="bg-green-100"
          isLoading={statsLoading}
        />

        <KPICard
          title="In Maintenance"
          value={maintenanceScheduled}
          description="Equipment under maintenance"
          icon={Wrench}
          iconColor="text-yellow-600"
          iconBgColor="bg-yellow-100"
          isLoading={statsLoading}
        />

        <KPICard
          title="Low Battery Sensors"
          value={lowBatterySensors}
          description="Sensors requiring attention"
          icon={Battery}
          iconColor="text-red-600"
          iconBgColor="bg-red-100"
          isLoading={statsLoading}
        />
      </div>

      {/* Charts */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Equipment Distribution */}
        <DonutChartCard
          title="Equipment Distribution"
          description="Distribution of pumps and sensors"
          data={equipmentDistribution}
          isLoading={statsLoading}
        />

        {/* Status Distribution */}
        <BarChartCard
          title="Status Distribution"
          description="Equipment status by type"
          data={statusDistribution}
          dataKeys={[
            { key: 'pumps', color: '#3b82f6', name: 'Pumps' },
            { key: 'sensors', color: '#10b981', name: 'Sensors' },
          ]}
          isLoading={statsLoading}
        />

        {/* Equipment Activity Trends */}
        <AreaChartCard
          title="Equipment Activity Trends"
          description="Active vs total equipment over time"
          data={activityTrends}
          dataKeys={[
            { key: 'active', color: '#10b981', name: 'Active' },
            { key: 'total', color: '#3b82f6', name: 'Total' },
          ]}
          xAxisKey="month"
          isLoading={statsLoading}
        />

        {/* Battery Status */}
        <DonutChartCard
          title="Sensor Battery Status"
          description="Battery level distribution across sensors"
          data={batteryDistribution}
          isLoading={statsLoading}
        />
      </div>
    </div>
  );
}
