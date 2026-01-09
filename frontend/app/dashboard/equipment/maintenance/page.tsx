'use client';

import { useState } from 'react';
import Link from 'next/link';
import { useMaintenanceSchedule, useScheduleMaintenance } from '@/hooks/use-equipment';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Skeleton } from '@/components/ui/skeleton';
import { Badge } from '@/components/ui/badge';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { MaintenanceForm } from '@/components/equipment/maintenance-form';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from '@/components/ui/dialog';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table';
import { Calendar, Wrench, Clock, FileText, Plus, Filter } from 'lucide-react';
import { formatDate, formatDateTime } from '@/lib/utils';

export default function MaintenancePage() {
  const [maintenanceDialogOpen, setMaintenanceDialogOpen] = useState(false);
  const { data: schedule, isLoading } = useMaintenanceSchedule();
  const scheduleMaintenance = useScheduleMaintenance();

  const handleMaintenanceSubmit = async (data: any) => {
    try {
      await scheduleMaintenance.mutateAsync({
        id: '1', // TODO: Get from form
        data: {
          scheduledDate: data.scheduledDate,
          notes: data.description,
        },
      });
      setMaintenanceDialogOpen(false);
    } catch (error) {
      // Error handled by hook
    }
  };

  const upcoming = (schedule && Array.isArray(schedule)) ? schedule.filter(m => m.status === 'SCHEDULED' || m.status === 'IN_PROGRESS') : [];
  const completed = (schedule && Array.isArray(schedule)) ? schedule.filter(m => m.status === 'COMPLETED') : [];

  return (
    <div className="space-y-6 max-w-7xl">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold">Maintenance Management</h1>
          <p className="text-muted-foreground mt-1">
            Schedule and track equipment maintenance
          </p>
        </div>
        <div className="flex items-center gap-2">
          <Button variant="outline">
            <Filter className="w-4 h-4 mr-2" />
            Filter
          </Button>
          <Dialog open={maintenanceDialogOpen} onOpenChange={setMaintenanceDialogOpen}>
            <DialogTrigger asChild>
              <Button>
                <Plus className="w-4 h-4 mr-2" />
                Schedule Maintenance
              </Button>
            </DialogTrigger>
            <DialogContent className="max-w-2xl">
              <DialogHeader>
                <DialogTitle>Schedule New Maintenance</DialogTitle>
                <DialogDescription>
                  Schedule maintenance for equipment
                </DialogDescription>
              </DialogHeader>
              <MaintenanceForm
                equipmentId="1"
                onSubmit={handleMaintenanceSubmit}
                isLoading={scheduleMaintenance.isPending}
              />
            </DialogContent>
          </Dialog>
        </div>
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <Card>
          <CardContent className="pt-6">
            <div className="flex items-center gap-4">
              <div className="w-12 h-12 rounded-lg bg-blue-100 flex items-center justify-center">
                <Calendar className="w-6 h-6 text-blue-600" />
              </div>
              <div>
                <p className="text-2xl font-bold">{upcoming.length}</p>
                <p className="text-sm text-muted-foreground">Upcoming</p>
              </div>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardContent className="pt-6">
            <div className="flex items-center gap-4">
              <div className="w-12 h-12 rounded-lg bg-green-100 flex items-center justify-center">
                <Wrench className="w-6 h-6 text-green-600" />
              </div>
              <div>
                <p className="text-2xl font-bold">{completed.length}</p>
                <p className="text-sm text-muted-foreground">Completed</p>
              </div>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardContent className="pt-6">
            <div className="flex items-center gap-4">
              <div className="w-12 h-12 rounded-lg bg-yellow-100 flex items-center justify-center">
                <Clock className="w-6 h-6 text-yellow-600" />
              </div>
              <div>
                <p className="text-2xl font-bold">
                  {upcoming.filter(m => new Date(m.scheduledDate) < new Date()).length}
                </p>
                <p className="text-sm text-muted-foreground">Overdue</p>
              </div>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardContent className="pt-6">
            <div className="flex items-center gap-4">
              <div className="w-12 h-12 rounded-lg bg-purple-100 flex items-center justify-center">
                <FileText className="w-6 h-6 text-purple-600" />
              </div>
              <div>
                <p className="text-2xl font-bold">{schedule?.length || 0}</p>
                <p className="text-sm text-muted-foreground">Total Records</p>
              </div>
            </div>
          </CardContent>
        </Card>
      </div>

      {/* Maintenance Tables */}
      <Tabs defaultValue="upcoming" className="w-full">
        <TabsList>
          <TabsTrigger value="upcoming">
            Upcoming ({upcoming.length})
          </TabsTrigger>
          <TabsTrigger value="completed">
            History ({completed.length})
          </TabsTrigger>
        </TabsList>

        <TabsContent value="upcoming" className="space-y-4">
          <Card>
            <CardHeader>
              <CardTitle>Scheduled Maintenance</CardTitle>
              <CardDescription>
                View and manage upcoming maintenance tasks
              </CardDescription>
            </CardHeader>
            <CardContent>
              {isLoading ? (
                <div className="space-y-2">
                  {[...Array(5)].map((_, i) => (
                    <Skeleton key={i} className="h-16 w-full" />
                  ))}
                </div>
              ) : upcoming.length === 0 ? (
                <div className="text-center py-12">
                  <Calendar className="w-12 h-12 text-muted-foreground mx-auto mb-4" />
                  <p className="text-muted-foreground">No upcoming maintenance scheduled</p>
                  <Button
                    variant="outline"
                    className="mt-4"
                    onClick={() => setMaintenanceDialogOpen(true)}
                  >
                    <Plus className="w-4 h-4 mr-2" />
                    Schedule Maintenance
                  </Button>
                </div>
              ) : (
                <Table>
                  <TableHeader>
                    <TableRow>
                      <TableHead>Equipment</TableHead>
                      <TableHead>Type</TableHead>
                      <TableHead>Scheduled Date</TableHead>
                      <TableHead>Description</TableHead>
                      <TableHead>Duration</TableHead>
                      <TableHead>Status</TableHead>
                      <TableHead className="text-right">Actions</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {upcoming.map((maintenance) => {
                      const isOverdue = new Date(maintenance.scheduledDate) < new Date();
                      return (
                        <TableRow key={maintenance.id}>
                          <TableCell className="font-medium">
                            <Link
                              href={`/dashboard/equipment/${maintenance.equipmentType.toLowerCase()}s/${maintenance.equipmentId}`}
                              className="hover:underline"
                            >
                              {maintenance.equipmentId}
                            </Link>
                          </TableCell>
                          <TableCell>
                            <Badge variant="outline">{maintenance.equipmentType}</Badge>
                          </TableCell>
                          <TableCell>
                            <div className="flex flex-col">
                              <span className={isOverdue ? 'text-red-600 font-medium' : ''}>
                                {formatDate(maintenance.scheduledDate)}
                              </span>
                              {isOverdue && (
                                <span className="text-xs text-red-600">Overdue</span>
                              )}
                            </div>
                          </TableCell>
                          <TableCell className="max-w-xs truncate">
                            {maintenance.description}
                          </TableCell>
                          <TableCell>
                            {maintenance.estimatedDuration ? `${maintenance.estimatedDuration}h` : '-'}
                          </TableCell>
                          <TableCell>
                            <Badge
                              variant={
                                maintenance.status === 'IN_PROGRESS'
                                  ? 'default'
                                  : 'secondary'
                              }
                            >
                              {maintenance.status}
                            </Badge>
                          </TableCell>
                          <TableCell className="text-right">
                            <Button variant="ghost" size="sm">
                              View
                            </Button>
                          </TableCell>
                        </TableRow>
                      );
                    })}
                  </TableBody>
                </Table>
              )}
            </CardContent>
          </Card>
        </TabsContent>

        <TabsContent value="completed" className="space-y-4">
          <Card>
            <CardHeader>
              <CardTitle>Maintenance History</CardTitle>
              <CardDescription>
                View completed maintenance records
              </CardDescription>
            </CardHeader>
            <CardContent>
              {isLoading ? (
                <div className="space-y-2">
                  {[...Array(5)].map((_, i) => (
                    <Skeleton key={i} className="h-16 w-full" />
                  ))}
                </div>
              ) : completed.length === 0 ? (
                <div className="text-center py-12">
                  <FileText className="w-12 h-12 text-muted-foreground mx-auto mb-4" />
                  <p className="text-muted-foreground">No maintenance history</p>
                </div>
              ) : (
                <Table>
                  <TableHeader>
                    <TableRow>
                      <TableHead>Equipment</TableHead>
                      <TableHead>Type</TableHead>
                      <TableHead>Completed Date</TableHead>
                      <TableHead>Description</TableHead>
                      <TableHead>Duration</TableHead>
                      <TableHead>Notes</TableHead>
                      <TableHead className="text-right">Actions</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {completed.map((maintenance) => (
                      <TableRow key={maintenance.id}>
                        <TableCell className="font-medium">
                          <Link
                            href={`/dashboard/equipment/${maintenance.equipmentType.toLowerCase()}s/${maintenance.equipmentId}`}
                            className="hover:underline"
                          >
                            {maintenance.equipmentId}
                          </Link>
                        </TableCell>
                        <TableCell>
                          <Badge variant="outline">{maintenance.equipmentType}</Badge>
                        </TableCell>
                        <TableCell>
                          {maintenance.completedDate
                            ? formatDate(maintenance.completedDate)
                            : '-'}
                        </TableCell>
                        <TableCell className="max-w-xs truncate">
                          {maintenance.description}
                        </TableCell>
                        <TableCell>
                          {maintenance.estimatedDuration ? `${maintenance.estimatedDuration}h` : '-'}
                        </TableCell>
                        <TableCell className="max-w-xs truncate">
                          {maintenance.notes || '-'}
                        </TableCell>
                        <TableCell className="text-right">
                          <Button variant="ghost" size="sm">
                            View
                          </Button>
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              )}
            </CardContent>
          </Card>
        </TabsContent>
      </Tabs>
    </div>
  );
}
