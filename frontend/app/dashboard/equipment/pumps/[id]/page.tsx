'use client';

import { useState } from 'react';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { usePump, useUpdatePump, useScheduleMaintenance } from '@/hooks/use-equipment';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Skeleton } from '@/components/ui/skeleton';
import { Badge } from '@/components/ui/badge';
import { Separator } from '@/components/ui/separator';
import { StatusBadge } from '@/components/equipment/status-badge';
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
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { ArrowLeft, Droplet, MapPin, Calendar, Wrench, Play, Pause, AlertTriangle } from 'lucide-react';
import { formatDate, formatDateTime } from '@/lib/utils';
import { toast } from 'sonner';

export default function PumpDetailPage({ params }: { params: { id: string } }) {
  const router = useRouter();
  const { data: pump, isLoading, isError } = usePump(params.id);
  const updatePump = useUpdatePump();
  const scheduleMaintenance = useScheduleMaintenance();
  const [maintenanceDialogOpen, setMaintenanceDialogOpen] = useState(false);

  const handleStatusChange = async (status: string) => {
    if (!pump) return;

    try {
      await updatePump.mutateAsync({
        id: params.id,
        data: { status: status as any },
      });
    } catch (error) {
      // Error handled by hook
    }
  };

  const handleMaintenanceSubmit = async (data: any) => {
    try {
      await scheduleMaintenance.mutateAsync({
        id: params.id,
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

  if (isError) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="text-center">
          <p className="text-destructive mb-2">Failed to load pump details</p>
          <Button onClick={() => router.push('/dashboard/equipment')}>Back to Equipment</Button>
        </div>
      </div>
    );
  }

  if (isLoading || !pump) {
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
        <div className="flex items-center gap-2">
          <Dialog open={maintenanceDialogOpen} onOpenChange={setMaintenanceDialogOpen}>
            <DialogTrigger asChild>
              <Button variant="outline">
                <Wrench className="w-4 h-4 mr-2" />
                Schedule Maintenance
              </Button>
            </DialogTrigger>
            <DialogContent className="max-w-2xl">
              <DialogHeader>
                <DialogTitle>Schedule Maintenance</DialogTitle>
                <DialogDescription>
                  Schedule maintenance for {pump.model}
                </DialogDescription>
              </DialogHeader>
              <MaintenanceForm
                equipmentId={params.id}
                onSubmit={handleMaintenanceSubmit}
                isLoading={scheduleMaintenance.isPending}
              />
            </DialogContent>
          </Dialog>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Main Info */}
        <Card className="lg:col-span-2">
          <CardHeader>
            <div className="flex items-start justify-between">
              <div className="flex items-center gap-4">
                <div className="w-16 h-16 rounded-lg bg-blue-100 flex items-center justify-center">
                  <Droplet className="w-8 h-8 text-blue-600" />
                </div>
                <div>
                  <CardTitle className="text-2xl">{pump.model}</CardTitle>
                  <p className="text-sm text-muted-foreground mt-1">Pump ID: {pump.id}</p>
                </div>
              </div>
              <StatusBadge status={pump.status} size="lg" />
            </div>
          </CardHeader>
          <CardContent className="space-y-6">
            {/* Status Control */}
            <div className="p-4 border rounded-lg bg-muted/50">
              <h3 className="font-semibold mb-3">Status Control</h3>
              <div className="flex items-center gap-4">
                <Select value={pump.status} onValueChange={handleStatusChange}>
                  <SelectTrigger className="w-[200px]">
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="ACTIVE">
                      <div className="flex items-center gap-2">
                        <Play className="w-4 h-4" />
                        Active
                      </div>
                    </SelectItem>
                    <SelectItem value="INACTIVE">
                      <div className="flex items-center gap-2">
                        <Pause className="w-4 h-4" />
                        Inactive
                      </div>
                    </SelectItem>
                    <SelectItem value="MAINTENANCE">
                      <div className="flex items-center gap-2">
                        <Wrench className="w-4 h-4" />
                        Maintenance
                      </div>
                    </SelectItem>
                  </SelectContent>
                </Select>
                <Badge variant={pump.operational ? 'default' : 'secondary'}>
                  {pump.operational ? 'Operational' : 'Not Operational'}
                </Badge>
              </div>
            </div>

            <Separator />

            {/* Specifications */}
            <div>
              <h3 className="font-semibold mb-4">Specifications</h3>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div className="flex items-start gap-3">
                  <MapPin className="w-5 h-5 text-muted-foreground mt-0.5" />
                  <div>
                    <p className="text-sm font-medium">Location</p>
                    <p className="text-sm text-muted-foreground">{pump.location}</p>
                  </div>
                </div>
                <div className="flex items-start gap-3">
                  <Droplet className="w-5 h-5 text-muted-foreground mt-0.5" />
                  <div>
                    <p className="text-sm font-medium">Maximum Flow Rate</p>
                    <p className="text-sm text-muted-foreground">{pump.formattedMaxFlow}</p>
                  </div>
                </div>
                <div className="flex items-start gap-3">
                  <Calendar className="w-5 h-5 text-muted-foreground mt-0.5" />
                  <div>
                    <p className="text-sm font-medium">Installation Date</p>
                    <p className="text-sm text-muted-foreground">{formatDate(pump.installationDate)}</p>
                  </div>
                </div>
                {pump.lastMaintenanceDate && (
                  <div className="flex items-start gap-3">
                    <Wrench className="w-5 h-5 text-muted-foreground mt-0.5" />
                    <div>
                      <p className="text-sm font-medium">Last Maintenance</p>
                      <p className="text-sm text-muted-foreground">{formatDate(pump.lastMaintenanceDate)}</p>
                    </div>
                  </div>
                )}
              </div>
            </div>

            {/* Maintenance Info */}
            {pump.nextMaintenanceDate && (
              <>
                <Separator />
                <div className="p-4 border rounded-lg bg-muted/50">
                  <div className="flex items-start gap-3">
                    <Calendar className="w-5 h-5 text-muted-foreground mt-0.5" />
                    <div className="flex-1">
                      <p className="text-sm font-medium mb-1">Next Scheduled Maintenance</p>
                      <p className="text-sm text-muted-foreground">{formatDate(pump.nextMaintenanceDate)}</p>
                      {pump.maintenanceOverdue && (
                        <Badge variant="outline" className="bg-yellow-50 text-yellow-700 border-yellow-200 mt-2">
                          <AlertTriangle className="w-3 h-3 mr-1" />
                          Overdue
                        </Badge>
                      )}
                    </div>
                  </div>
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
              <StatusBadge status={pump.status} size="sm" />
            </div>
            <Separator />
            <div className="flex items-center justify-between">
              <span className="text-sm text-muted-foreground">Operational</span>
              <Badge variant={pump.operational ? 'default' : 'secondary'} className="text-xs">
                {pump.operational ? 'Yes' : 'No'}
              </Badge>
            </div>
            <Separator />
            <div className="flex items-center justify-between">
              <span className="text-sm text-muted-foreground">Farm ID</span>
              <span className="text-sm font-medium">{pump.farmId}</span>
            </div>
            <Separator />
            <div className="flex items-center justify-between">
              <span className="text-sm text-muted-foreground">Max Flow</span>
              <span className="text-sm font-medium">{pump.maxFlow} L/min</span>
            </div>
            {pump.maintenanceOverdue && (
              <>
                <Separator />
                <div className="p-3 bg-yellow-50 border border-yellow-200 rounded-lg">
                  <p className="text-xs font-medium text-yellow-800 mb-1">⚠️ Attention Required</p>
                  <p className="text-xs text-yellow-700">Maintenance is overdue</p>
                </div>
              </>
            )}
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
