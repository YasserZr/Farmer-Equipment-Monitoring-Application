'use client';

import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { Calendar, Clock, Wrench, ChevronRight } from 'lucide-react';
import { formatDate } from '@/lib/utils';
import Link from 'next/link';

interface MaintenanceItem {
  id: string;
  equipmentId: string;
  equipmentType: string;
  scheduledDate: string;
  description: string;
  estimatedDuration?: number;
  status: 'SCHEDULED' | 'IN_PROGRESS' | 'COMPLETED';
}

interface MaintenanceTimelineProps {
  items: MaintenanceItem[];
  isLoading?: boolean;
}

export function MaintenanceTimeline({ items, isLoading }: MaintenanceTimelineProps) {
  const getStatusColor = (status: string) => {
    switch (status) {
      case 'SCHEDULED':
        return 'bg-blue-100 text-blue-800 border-blue-200';
      case 'IN_PROGRESS':
        return 'bg-yellow-100 text-yellow-800 border-yellow-200';
      case 'COMPLETED':
        return 'bg-green-100 text-green-800 border-green-200';
      default:
        return 'bg-gray-100 text-gray-800 border-gray-200';
    }
  };

  const isOverdue = (date: string) => {
    return new Date(date) < new Date();
  };

  if (isLoading) {
    return (
      <Card>
        <CardHeader>
          <div className="h-6 bg-gray-200 rounded w-48 animate-pulse"></div>
        </CardHeader>
        <CardContent>
          <div className="space-y-4">
            {[...Array(4)].map((_, i) => (
              <div key={i} className="flex items-start gap-4 p-4 border rounded-lg animate-pulse">
                <div className="w-10 h-10 bg-gray-200 rounded-lg"></div>
                <div className="flex-1 space-y-2">
                  <div className="h-4 bg-gray-200 rounded w-3/4"></div>
                  <div className="h-3 bg-gray-200 rounded w-1/2"></div>
                  <div className="h-3 bg-gray-200 rounded w-1/4"></div>
                </div>
              </div>
            ))}
          </div>
        </CardContent>
      </Card>
    );
  }

  const upcomingItems = items
    .filter(item => item.status !== 'COMPLETED')
    .sort((a, b) => new Date(a.scheduledDate).getTime() - new Date(b.scheduledDate).getTime())
    .slice(0, 6);

  return (
    <Card>
      <CardHeader>
        <div className="flex items-center justify-between">
          <div>
            <CardTitle>Upcoming Maintenance</CardTitle>
            <CardDescription>Scheduled maintenance tasks</CardDescription>
          </div>
          <Button variant="ghost" size="sm" asChild>
            <Link href="/dashboard/equipment/maintenance">
              View All
              <ChevronRight className="w-4 h-4 ml-1" />
            </Link>
          </Button>
        </div>
      </CardHeader>
      <CardContent>
        {upcomingItems.length > 0 ? (
          <div className="space-y-4">
            {upcomingItems.map((item, index) => {
              const overdue = isOverdue(item.scheduledDate) && item.status === 'SCHEDULED';
              return (
                <div
                  key={item.id}
                  className={`relative flex items-start gap-4 p-4 border rounded-lg hover:bg-muted/50 transition-colors ${
                    overdue ? 'border-red-200 bg-red-50/50' : ''
                  }`}
                >
                  {/* Timeline connector */}
                  {index < upcomingItems.length - 1 && (
                    <div className="absolute left-9 top-16 bottom-0 w-px bg-border"></div>
                  )}

                  {/* Icon */}
                  <div className={`p-2 rounded-lg ${overdue ? 'bg-red-100' : 'bg-blue-100'}`}>
                    <Wrench className={`w-6 h-6 ${overdue ? 'text-red-600' : 'text-blue-600'}`} />
                  </div>

                  {/* Content */}
                  <div className="flex-1 min-w-0">
                    <div className="flex items-start justify-between gap-2 mb-2">
                      <div className="flex-1">
                        <p className="text-sm font-medium">{item.description}</p>
                        <p className="text-xs text-muted-foreground mt-1">
                          {item.equipmentType}: {item.equipmentId}
                        </p>
                      </div>
                      <Badge variant="outline" className={getStatusColor(item.status)}>
                        {item.status.replace('_', ' ')}
                      </Badge>
                    </div>

                    <div className="flex items-center gap-4 text-xs text-muted-foreground">
                      <div className="flex items-center gap-1">
                        <Calendar className="w-3 h-3" />
                        <span className={overdue ? 'text-red-600 font-medium' : ''}>
                          {formatDate(item.scheduledDate)}
                        </span>
                        {overdue && (
                          <Badge variant="outline" className="ml-1 text-xs bg-red-50 text-red-700 border-red-200">
                            Overdue
                          </Badge>
                        )}
                      </div>
                      {item.estimatedDuration && (
                        <div className="flex items-center gap-1">
                          <Clock className="w-3 h-3" />
                          <span>{item.estimatedDuration}h</span>
                        </div>
                      )}
                    </div>
                  </div>
                </div>
              );
            })}
          </div>
        ) : (
          <div className="text-center py-8">
            <div className="w-12 h-12 rounded-full bg-green-100 mx-auto mb-3 flex items-center justify-center">
              <Wrench className="w-6 h-6 text-green-600" />
            </div>
            <p className="text-sm text-muted-foreground">No upcoming maintenance</p>
            <p className="text-xs text-muted-foreground mt-1">All equipment is up to date</p>
          </div>
        )}
      </CardContent>
    </Card>
  );
}
