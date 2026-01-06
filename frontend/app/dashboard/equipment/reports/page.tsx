'use client';

import { useState } from 'react';
import { useEquipmentStatistics, usePumps, useSensors } from '@/hooks/use-equipment';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';
import { Skeleton } from '@/components/ui/skeleton';
import { Button } from '@/components/ui/button';
import {
  BarChart,
  Bar,
  LineChart,
  Line,
  PieChart,
  Pie,
  Cell,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
} from 'recharts';
import { Download, FileText, Activity, AlertTriangle, Battery, Wrench } from 'lucide-react';

const COLORS = ['#3b82f6', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6'];

export default function ReportsPage() {
  const { data: statistics, isLoading: statsLoading } = useEquipmentStatistics();
  const { data: pumpsData } = usePumps(1, 100);
  const { data: sensorsData } = useSensors(1, 100);

  const pumps = pumpsData?.content || [];
  const sensors = sensorsData?.content || [];

  // Equipment Distribution
  const equipmentDistribution = [
    { name: 'Pumps', value: pumps.length },
    { name: 'Sensors', value: sensors.length },
  ];

  // Status Distribution
  const statusDistribution = [
    {
      name: 'Active',
      pumps: pumps.filter(p => p.status === 'ACTIVE').length,
      sensors: sensors.filter(s => s.active).length,
    },
    {
      name: 'Inactive',
      pumps: pumps.filter(p => p.status === 'INACTIVE').length,
      sensors: sensors.filter(s => !s.active).length,
    },
    {
      name: 'Maintenance',
      pumps: pumps.filter(p => p.status === 'MAINTENANCE').length,
      sensors: 0,
    },
  ];

  // Battery Status Distribution
  const batteryDistribution = [
    { name: 'Good (>50%)', value: sensors.filter(s => s.batteryLevel > 50).length },
    { name: 'Low (20-50%)', value: sensors.filter(s => s.batteryLevel >= 20 && s.batteryLevel <= 50).length },
    { name: 'Critical (<20%)', value: sensors.filter(s => s.batteryLevel < 20).length },
  ];

  // Maintenance Trends (Mock data - replace with actual API data)
  const maintenanceTrends = [
    { month: 'Jan', scheduled: 4, completed: 3 },
    { month: 'Feb', scheduled: 6, completed: 5 },
    { month: 'Mar', scheduled: 5, completed: 4 },
    { month: 'Apr', scheduled: 8, completed: 7 },
    { month: 'May', scheduled: 7, completed: 6 },
    { month: 'Jun', scheduled: 9, completed: 8 },
  ];

  const totalEquipment = pumps.length + sensors.length;
  const activeEquipment = pumps.filter(p => p.status === 'ACTIVE').length + 
                         sensors.filter(s => s.active).length;
  const maintenanceScheduled = pumps.filter(p => p.status === 'MAINTENANCE').length;
  const lowBatterySensors = sensors.filter(s => s.batteryLow || s.batteryCritical).length;

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
        <Card>
          <CardContent className="pt-6">
            <div className="flex items-center gap-4">
              <div className="w-12 h-12 rounded-lg bg-blue-100 flex items-center justify-center">
                <Activity className="w-6 h-6 text-blue-600" />
              </div>
              <div>
                <p className="text-2xl font-bold">{totalEquipment}</p>
                <p className="text-sm text-muted-foreground">Total Equipment</p>
              </div>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardContent className="pt-6">
            <div className="flex items-center gap-4">
              <div className="w-12 h-12 rounded-lg bg-green-100 flex items-center justify-center">
                <Activity className="w-6 h-6 text-green-600" />
              </div>
              <div>
                <p className="text-2xl font-bold">{activeEquipment}</p>
                <p className="text-sm text-muted-foreground">Active Equipment</p>
              </div>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardContent className="pt-6">
            <div className="flex items-center gap-4">
              <div className="w-12 h-12 rounded-lg bg-yellow-100 flex items-center justify-center">
                <Wrench className="w-6 h-6 text-yellow-600" />
              </div>
              <div>
                <p className="text-2xl font-bold">{maintenanceScheduled}</p>
                <p className="text-sm text-muted-foreground">In Maintenance</p>
              </div>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardContent className="pt-6">
            <div className="flex items-center gap-4">
              <div className="w-12 h-12 rounded-lg bg-red-100 flex items-center justify-center">
                <Battery className="w-6 h-6 text-red-600" />
              </div>
              <div>
                <p className="text-2xl font-bold">{lowBatterySensors}</p>
                <p className="text-sm text-muted-foreground">Low Battery Sensors</p>
              </div>
            </div>
          </CardContent>
        </Card>
      </div>

      {/* Charts */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Equipment Distribution */}
        <Card>
          <CardHeader>
            <CardTitle>Equipment Distribution</CardTitle>
            <CardDescription>
              Distribution of pumps and sensors
            </CardDescription>
          </CardHeader>
          <CardContent>
            {statsLoading ? (
              <Skeleton className="h-[300px] w-full" />
            ) : (
              <ResponsiveContainer width="100%" height={300}>
                <PieChart>
                  <Pie
                    data={equipmentDistribution}
                    cx="50%"
                    cy="50%"
                    labelLine={false}
                    label={({ name, percent }) => `${name}: ${(percent * 100).toFixed(0)}%`}
                    outerRadius={100}
                    fill="#8884d8"
                    dataKey="value"
                  >
                    {equipmentDistribution.map((entry, index) => (
                      <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                    ))}
                  </Pie>
                  <Tooltip />
                  <Legend />
                </PieChart>
              </ResponsiveContainer>
            )}
          </CardContent>
        </Card>

        {/* Status Distribution */}
        <Card>
          <CardHeader>
            <CardTitle>Status Distribution</CardTitle>
            <CardDescription>
              Equipment status by type
            </CardDescription>
          </CardHeader>
          <CardContent>
            {statsLoading ? (
              <Skeleton className="h-[300px] w-full" />
            ) : (
              <ResponsiveContainer width="100%" height={300}>
                <BarChart data={statusDistribution}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="name" />
                  <YAxis />
                  <Tooltip />
                  <Legend />
                  <Bar dataKey="pumps" fill="#3b82f6" name="Pumps" />
                  <Bar dataKey="sensors" fill="#10b981" name="Sensors" />
                </BarChart>
              </ResponsiveContainer>
            )}
          </CardContent>
        </Card>

        {/* Maintenance Trends */}
        <Card>
          <CardHeader>
            <CardTitle>Maintenance Trends</CardTitle>
            <CardDescription>
              Scheduled vs completed maintenance over time
            </CardDescription>
          </CardHeader>
          <CardContent>
            {statsLoading ? (
              <Skeleton className="h-[300px] w-full" />
            ) : (
              <ResponsiveContainer width="100%" height={300}>
                <LineChart data={maintenanceTrends}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="month" />
                  <YAxis />
                  <Tooltip />
                  <Legend />
                  <Line 
                    type="monotone" 
                    dataKey="scheduled" 
                    stroke="#3b82f6" 
                    name="Scheduled"
                    strokeWidth={2}
                  />
                  <Line 
                    type="monotone" 
                    dataKey="completed" 
                    stroke="#10b981" 
                    name="Completed"
                    strokeWidth={2}
                  />
                </LineChart>
              </ResponsiveContainer>
            )}
          </CardContent>
        </Card>

        {/* Battery Status */}
        <Card>
          <CardHeader>
            <CardTitle>Sensor Battery Status</CardTitle>
            <CardDescription>
              Battery level distribution across sensors
            </CardDescription>
          </CardHeader>
          <CardContent>
            {statsLoading ? (
              <Skeleton className="h-[300px] w-full" />
            ) : (
              <ResponsiveContainer width="100%" height={300}>
                <BarChart data={batteryDistribution}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="name" />
                  <YAxis />
                  <Tooltip />
                  <Bar dataKey="value" fill="#8b5cf6" name="Sensors">
                    {batteryDistribution.map((entry, index) => (
                      <Cell 
                        key={`cell-${index}`} 
                        fill={
                          entry.name.includes('Good') ? '#10b981' :
                          entry.name.includes('Low') ? '#f59e0b' :
                          '#ef4444'
                        } 
                      />
                    ))}
                  </Bar>
                </BarChart>
              </ResponsiveContainer>
            )}
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
