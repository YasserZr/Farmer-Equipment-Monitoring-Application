'use client';

import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { Battery, AlertTriangle, WifiOff, Wrench, ChevronRight } from 'lucide-react';
import Link from 'next/link';

interface Alert {
  id: string;
  type: 'battery' | 'offline' | 'maintenance' | 'critical';
  equipmentId: string;
  equipmentType: string;
  message: string;
  severity: 'high' | 'medium' | 'low';
  timestamp: string;
}

interface AlertsPanelProps {
  alerts: Alert[];
  isLoading?: boolean;
}

export function AlertsPanel({ alerts, isLoading }: AlertsPanelProps) {
  const getAlertIcon = (type: string) => {
    switch (type) {
      case 'battery':
        return Battery;
      case 'offline':
        return WifiOff;
      case 'maintenance':
        return Wrench;
      case 'critical':
        return AlertTriangle;
      default:
        return AlertTriangle;
    }
  };

  const getAlertColor = (severity: string) => {
    switch (severity) {
      case 'high':
        return 'bg-red-100 text-red-800 border-red-200';
      case 'medium':
        return 'bg-yellow-100 text-yellow-800 border-yellow-200';
      case 'low':
        return 'bg-blue-100 text-blue-800 border-blue-200';
      default:
        return 'bg-gray-100 text-gray-800 border-gray-200';
    }
  };

  const getIconColor = (severity: string) => {
    switch (severity) {
      case 'high':
        return 'text-red-600 bg-red-100';
      case 'medium':
        return 'text-yellow-600 bg-yellow-100';
      case 'low':
        return 'text-blue-600 bg-blue-100';
      default:
        return 'text-gray-600 bg-gray-100';
    }
  };

  if (isLoading) {
    return (
      <Card>
        <CardHeader>
          <div className="flex items-center justify-between">
            <div className="h-6 bg-gray-200 rounded w-32 animate-pulse"></div>
            <div className="h-6 bg-gray-200 rounded-full w-8 animate-pulse"></div>
          </div>
        </CardHeader>
        <CardContent>
          <div className="space-y-4">
            {[...Array(5)].map((_, i) => (
              <div key={i} className="flex items-center gap-4 p-4 border rounded-lg animate-pulse">
                <div className="w-10 h-10 bg-gray-200 rounded-lg"></div>
                <div className="flex-1 space-y-2">
                  <div className="h-4 bg-gray-200 rounded w-3/4"></div>
                  <div className="h-3 bg-gray-200 rounded w-1/2"></div>
                </div>
              </div>
            ))}
          </div>
        </CardContent>
      </Card>
    );
  }

  const highPriorityCount = alerts.filter(a => a.severity === 'high').length;

  return (
    <Card>
      <CardHeader>
        <div className="flex items-center justify-between">
          <CardTitle className="flex items-center gap-2">
            Active Alerts
            {highPriorityCount > 0 && (
              <Badge variant="destructive" className="rounded-full">
                {highPriorityCount}
              </Badge>
            )}
          </CardTitle>
          <Button variant="ghost" size="sm" asChild>
            <Link href="/dashboard/events">
              View All
              <ChevronRight className="w-4 h-4 ml-1" />
            </Link>
          </Button>
        </div>
      </CardHeader>
      <CardContent>
        {alerts.length > 0 ? (
          <div className="space-y-3">
            {alerts.slice(0, 8).map((alert) => {
              const Icon = getAlertIcon(alert.type);
              return (
                <div
                  key={alert.id}
                  className="flex items-start gap-4 p-3 border rounded-lg hover:bg-muted/50 transition-colors cursor-pointer"
                >
                  <div className={`p-2 rounded-lg ${getIconColor(alert.severity)}`}>
                    <Icon className="w-5 h-5" />
                  </div>
                  <div className="flex-1 min-w-0">
                    <div className="flex items-start justify-between gap-2">
                      <div className="flex-1">
                        <p className="text-sm font-medium">{alert.message}</p>
                        <p className="text-xs text-muted-foreground mt-1">
                          {alert.equipmentType}: {alert.equipmentId}
                        </p>
                      </div>
                      <Badge variant="outline" className={`text-xs ${getAlertColor(alert.severity)}`}>
                        {alert.severity}
                      </Badge>
                    </div>
                  </div>
                </div>
              );
            })}
          </div>
        ) : (
          <div className="text-center py-8">
            <div className="w-12 h-12 rounded-full bg-green-100 mx-auto mb-3 flex items-center justify-center">
              <AlertTriangle className="w-6 h-6 text-green-600" />
            </div>
            <p className="text-sm text-muted-foreground">No active alerts</p>
            <p className="text-xs text-muted-foreground mt-1">All systems are running smoothly</p>
          </div>
        )}
      </CardContent>
    </Card>
  );
}
