'use client';

import { useState } from 'react';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { useSensor, useUpdateSensor } from '@/hooks/use-equipment';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Skeleton } from '@/components/ui/skeleton';
import { Badge } from '@/components/ui/badge';
import { Separator } from '@/components/ui/separator';
import { BatteryIndicator } from '@/components/equipment/battery-indicator';
import { Progress } from '@/components/ui/progress';
import { ArrowLeft, Activity, MapPin, Calendar, Wifi, WifiOff, Gauge, AlertTriangle } from 'lucide-react';
import { formatDate, formatDateTime, formatRelativeTime } from '@/lib/utils';

export default function SensorDetailPage({ params }: { params: { id: string } }) {
  const router = useRouter();
  const { data: sensor, isLoading, isError } = useSensor(params.id);

  if (isError) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="text-center">
          <p className="text-destructive mb-2">Failed to load sensor details</p>
          <Button onClick={() => router.push('/dashboard/equipment')}>Back to Equipment</Button>
        </div>
      </div>
    );
  }

  if (isLoading || !sensor) {
    return (
      <div className="space-y-6">
        <Skeleton className="h-12 w-full" />
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          <Skeleton className="h-96 lg:col-span-2" />
          <Skeleton className="h-96" />
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-6 max-w-7xl">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-4">
          <Button variant="ghost" size="sm" asChild>
            <Link href="/dashboard/equipment">
              <ArrowLeft className="w-4 h-4 mr-2" />
              Back to Equipment
            </Link>
          </Button>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Main Info */}
        <Card className="lg:col-span-2">
          <CardHeader>
            <div className="flex items-start justify-between">
              <div className="flex items-center gap-4">
                <div className={`w-16 h-16 rounded-lg flex items-center justify-center ${
                  sensor.online ? 'bg-green-100' : 'bg-gray-100'
                }`}>
                  <Activity className={`w-8 h-8 ${sensor.online ? 'text-green-600' : 'text-gray-400'}`} />
                </div>
                <div>
                  <CardTitle className="text-2xl">{sensor.type}</CardTitle>
                  <p className="text-sm text-muted-foreground mt-1">{sensor.model}</p>
                  <p className="text-xs text-muted-foreground">ID: {sensor.id}</p>
                </div>
              </div>
              <Badge variant={sensor.online ? 'default' : 'secondary'} className="gap-1">
                {sensor.online ? <Wifi className="w-3 h-3" /> : <WifiOff className="w-3 h-3" />}
                {sensor.online ? 'Online' : 'Offline'}
              </Badge>
            </div>
          </CardHeader>
          <CardContent className="space-y-6">
            {/* Battery Status */}
            <div className="p-4 border rounded-lg bg-muted/50">
              <h3 className="font-semibold mb-3">Battery Status</h3>
              <BatteryIndicator 
                level={sensor.batteryLevel} 
                status={sensor.batteryStatus}
                size="lg"
              />
              <div className="mt-4 grid grid-cols-3 gap-4 text-center">
                <div>
                  <p className="text-2xl font-bold">{sensor.batteryLevel}%</p>
                  <p className="text-xs text-muted-foreground">Level</p>
                </div>
                <div>
                  <p className="text-2xl font-bold">{sensor.batteryStatus}</p>
                  <p className="text-xs text-muted-foreground">Status</p>
                </div>
                <div>
                  <p className="text-2xl font-bold">{sensor.online ? 'Yes' : 'No'}</p>
                  <p className="text-xs text-muted-foreground">Active</p>
                </div>
              </div>
            </div>

            <Separator />

            {/* Sensor Information */}
            <div>
              <h3 className="font-semibold mb-4">Sensor Information</h3>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div className="flex items-start gap-3">
                  <MapPin className="w-5 h-5 text-muted-foreground mt-0.5" />
                  <div>
                    <p className="text-sm font-medium">Location</p>
                    <p className="text-sm text-muted-foreground">{sensor.location}</p>
                  </div>
                </div>
                <div className="flex items-start gap-3">
                  <Gauge className="w-5 h-5 text-muted-foreground mt-0.5" />
                  <div>
                    <p className="text-sm font-medium">Sensor Type</p>
                    <p className="text-sm text-muted-foreground">{sensor.type}</p>
                  </div>
                </div>
                <div className="flex items-start gap-3">
                  <Calendar className="w-5 h-5 text-muted-foreground mt-0.5" />
                  <div>
                    <p className="text-sm font-medium">Last Communication</p>
                    <p className="text-sm text-muted-foreground">
                      {formatDateTime(sensor.lastCommunication)}
                    </p>
                    <p className="text-xs text-muted-foreground">
                      ({formatRelativeTime(sensor.lastCommunication)})
                    </p>
                  </div>
                </div>
                {sensor.alertThreshold && (
                  <div className="flex items-start gap-3">
                    <AlertTriangle className="w-5 h-5 text-muted-foreground mt-0.5" />
                    <div>
                      <p className="text-sm font-medium">Alert Threshold</p>
                      <p className="text-sm text-muted-foreground">
                        {sensor.alertThreshold} {sensor.unit}
                      </p>
                    </div>
                  </div>
                )}
              </div>
            </div>

            {/* Alerts */}
            {(sensor.batteryLow || sensor.batteryCritical || !sensor.online) && (
              <>
                <Separator />
                <div className="space-y-3">
                  <h3 className="font-semibold">Active Alerts</h3>
                  {sensor.batteryCritical && (
                    <div className="p-3 bg-red-50 border border-red-200 rounded-lg">
                      <p className="text-sm font-medium text-red-800 mb-1">
                        <AlertTriangle className="w-4 h-4 inline mr-1" />
                        Critical Battery Level
                      </p>
                      <p className="text-xs text-red-700">
                        Battery is at {sensor.batteryLevel}%. Immediate replacement required.
                      </p>
                    </div>
                  )}
                  {sensor.batteryLow && !sensor.batteryCritical && (
                    <div className="p-3 bg-yellow-50 border border-yellow-200 rounded-lg">
                      <p className="text-sm font-medium text-yellow-800 mb-1">
                        <AlertTriangle className="w-4 h-4 inline mr-1" />
                        Low Battery Level
                      </p>
                      <p className="text-xs text-yellow-700">
                        Battery is at {sensor.batteryLevel}%. Consider replacing soon.
                      </p>
                    </div>
                  )}
                  {!sensor.online && (
                    <div className="p-3 bg-gray-50 border border-gray-200 rounded-lg">
                      <p className="text-sm font-medium text-gray-800 mb-1">
                        <WifiOff className="w-4 h-4 inline mr-1" />
                        Sensor Offline
                      </p>
                      <p className="text-xs text-gray-700">
                        No communication received. Check sensor connection.
                      </p>
                    </div>
                  )}
                </div>
              </>
            )}
          </CardContent>
        </Card>

        {/* Quick Stats */}
        <Card>
          <CardHeader>
            <CardTitle>Quick Stats</CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            <div className="flex items-center justify-between">
              <span className="text-sm text-muted-foreground">Status</span>
              <Badge variant={sensor.active ? 'default' : 'secondary'}>
                {sensor.active ? 'Active' : 'Inactive'}
              </Badge>
            </div>
            <Separator />
            <div className="flex items-center justify-between">
              <span className="text-sm text-muted-foreground">Online</span>
              <Badge variant={sensor.online ? 'default' : 'secondary'} className="text-xs">
                {sensor.online ? 'Yes' : 'No'}
              </Badge>
            </div>
            <Separator />
            <div className="flex items-center justify-between">
              <span className="text-sm text-muted-foreground">Farm ID</span>
              <span className="text-sm font-medium">{sensor.farmId}</span>
            </div>
            <Separator />
            <div className="flex items-center justify-between">
              <span className="text-sm text-muted-foreground">Battery</span>
              <span className="text-sm font-medium">{sensor.batteryLevel}%</span>
            </div>
            {sensor.alertThreshold && (
              <>
                <Separator />
                <div className="flex items-center justify-between">
                  <span className="text-sm text-muted-foreground">Threshold</span>
                  <span className="text-sm font-medium">
                    {sensor.alertThreshold} {sensor.unit}
                  </span>
                </div>
              </>
            )}
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
