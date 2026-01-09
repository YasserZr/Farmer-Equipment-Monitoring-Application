'use client';

import { useState } from 'react';
import Link from 'next/link';
import { usePumps, useSensors } from '@/hooks/use-equipment';
import { useWebSocket } from '@/hooks/use-websocket';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { Badge } from '@/components/ui/badge';
import { Skeleton } from '@/components/ui/skeleton';
import { StatusBadge } from '@/components/equipment/status-badge';
import { BatteryIndicator } from '@/components/equipment/battery-indicator';
import { Search, Droplet, Activity, Filter, Wifi, WifiOff, MapPin, Wrench } from 'lucide-react';
import { formatDate, formatDateTime } from '@/lib/utils';

export default function EquipmentPage() {
  const [searchQuery, setSearchQuery] = useState('');
  const [statusFilter, setStatusFilter] = useState<string>('all');
  const [pumpPage, setPumpPage] = useState(0);
  const [sensorPage, setSensorPage] = useState(0);

  const { data: pumpsData, isLoading: pumpsLoading } = usePumps(pumpPage, 10);
  const { data: sensorsData, isLoading: sensorsLoading } = useSensors(sensorPage, 10);
  const { isConnected } = useWebSocket({ enabled: true });

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold">Equipment Monitoring</h1>
          <p className="mt-2 text-muted-foreground">
            Monitor and manage all agricultural equipment in real-time
          </p>
        </div>
        <div className="flex items-center gap-2">
          <Badge variant={isConnected ? 'default' : 'secondary'} className="gap-1">
            {isConnected ? <Wifi className="w-3 h-3" /> : <WifiOff className="w-3 h-3" />}
            {isConnected ? 'Live' : 'Offline'}
          </Badge>
          <Button asChild>
            <Link href="/dashboard/equipment/maintenance">
              <Wrench className="w-4 h-4 mr-2" />
              Maintenance
            </Link>
          </Button>
        </div>
      </div>

      {/* Search and Filters */}
      <Card>
        <CardContent className="pt-6">
          <div className="flex flex-col md:flex-row gap-4">
            <div className="relative flex-1">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground" />
              <Input
                placeholder="Search equipment by model, location..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="pl-10"
              />
            </div>
            <Select value={statusFilter} onValueChange={setStatusFilter}>
              <SelectTrigger className="w-full md:w-[200px]">
                <SelectValue placeholder="Filter by status" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">All Status</SelectItem>
                <SelectItem value="ACTIVE">Active</SelectItem>
                <SelectItem value="INACTIVE">Inactive</SelectItem>
                <SelectItem value="MAINTENANCE">Maintenance</SelectItem>
              </SelectContent>
            </Select>
            <Button variant="outline">
              <Filter className="w-4 h-4 mr-2" />
              More Filters
            </Button>
          </div>
        </CardContent>
      </Card>

      {/* Equipment Tabs */}
      <Tabs defaultValue="pumps" className="space-y-6">
        <TabsList>
          <TabsTrigger value="pumps">
            <Droplet className="w-4 h-4 mr-2" />
            Pumps ({pumpsData?.totalElements || 0})
          </TabsTrigger>
          <TabsTrigger value="sensors">
            <Activity className="w-4 h-4 mr-2" />
            Sensors ({sensorsData?.totalElements || 0})
          </TabsTrigger>
        </TabsList>

        {/* Pumps Tab */}
        <TabsContent value="pumps" className="space-y-4">
          {pumpsLoading ? (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {Array.from({ length: 6 }).map((_, i) => (
                <Skeleton key={i} className="h-48" />
              ))}
            </div>
          ) : pumpsData && pumpsData.content.length > 0 ? (
            <>
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                {pumpsData.content.map((pump) => (
                  <Card key={pump.id} className="hover:shadow-lg transition-shadow">
                    <CardHeader>
                      <div className="flex items-start justify-between">
                        <div className="flex items-center gap-3">
                          <div className="w-12 h-12 rounded-lg bg-blue-100 flex items-center justify-center">
                            <Droplet className="w-6 h-6 text-blue-600" />
                          </div>
                          <div>
                            <CardTitle className="text-lg">{pump.model}</CardTitle>
                            <p className="text-sm text-muted-foreground">ID: {pump.id}</p>
                          </div>
                        </div>
                        <StatusBadge status={pump.status} />
                      </div>
                    </CardHeader>
                    <CardContent className="space-y-3">
                      <div className="flex items-center gap-2 text-sm">
                        <MapPin className="w-4 h-4 text-muted-foreground" />
                        <span className="text-muted-foreground">{pump.location}</span>
                      </div>
                      <div className="flex justify-between text-sm">
                        <span className="text-muted-foreground">Max Flow:</span>
                        <span className="font-medium">{pump.formattedMaxFlow}</span>
                      </div>
                      <div className="flex justify-between text-sm">
                        <span className="text-muted-foreground">Status:</span>
                        <Badge variant={pump.operational ? 'default' : 'secondary'}>
                          {pump.operational ? 'Operational' : 'Not Operational'}
                        </Badge>
                      </div>
                      {pump.maintenanceOverdue && (
                        <div className="pt-3 border-t">
                          <Badge variant="outline" className="bg-yellow-50 text-yellow-700 border-yellow-200">
                            ⚠️ Maintenance Overdue
                          </Badge>
                        </div>
                      )}
                      <div className="pt-3 flex justify-end">
                        <Button variant="outline" size="sm" asChild>
                          <Link href={`/dashboard/equipment/pumps/${pump.id}`}>View Details</Link>
                        </Button>
                      </div>
                    </CardContent>
                  </Card>
                ))}
              </div>

              {/* Pagination */}
              {pumpsData.totalPages > 1 && (
                <div className="flex items-center justify-center gap-2">
                  <Button
                    variant="outline"
                    onClick={() => setPumpPage(pumpPage - 1)}
                    disabled={pumpsData.first}
                  >
                    Previous
                  </Button>
                  <span className="text-sm text-muted-foreground">
                    Page {pumpPage + 1} of {pumpsData.totalPages}
                  </span>
                  <Button
                    variant="outline"
                    onClick={() => setPumpPage(pumpPage + 1)}
                    disabled={pumpsData.last}
                  >
                    Next
                  </Button>
                </div>
              )}
            </>
          ) : (
            <div className="text-center py-12">
              <Droplet className="w-12 h-12 mx-auto text-muted-foreground mb-4" />
              <p className="text-muted-foreground">No pumps found</p>
            </div>
          )}
        </TabsContent>

        {/* Sensors Tab */}
        <TabsContent value="sensors" className="space-y-4">
          {sensorsLoading ? (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {Array.from({ length: 6 }).map((_, i) => (
                <Skeleton key={i} className="h-48" />
              ))}
            </div>
          ) : sensorsData && sensorsData.content.length > 0 ? (
            <>
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                {sensorsData.content.map((sensor) => (
                  <Card key={sensor.id} className="hover:shadow-lg transition-shadow">
                    <CardHeader>
                      <div className="flex items-start justify-between">
                        <div className="flex items-center gap-3">
                          <div className={`w-12 h-12 rounded-lg flex items-center justify-center ${
                            sensor.online ? 'bg-green-100' : 'bg-gray-100'
                          }`}>
                            <Activity className={`w-6 h-6 ${sensor.online ? 'text-green-600' : 'text-gray-400'}`} />
                          </div>
                          <div>
                            <CardTitle className="text-lg">{sensor.type}</CardTitle>
                            <p className="text-sm text-muted-foreground">{sensor.model}</p>
                          </div>
                        </div>
                        <Badge variant={sensor.online ? 'default' : 'secondary'}>
                          {sensor.online ? 'Online' : 'Offline'}
                        </Badge>
                      </div>
                    </CardHeader>
                    <CardContent className="space-y-3">
                      <div className="flex items-center gap-2 text-sm">
                        <MapPin className="w-4 h-4 text-muted-foreground" />
                        <span className="text-muted-foreground">{sensor.location}</span>
                      </div>
                      <div>
                        <div className="flex justify-between text-sm mb-2">
                          <span className="text-muted-foreground">Battery:</span>
                          <span className="text-xs text-muted-foreground">{sensor.batteryStatus}</span>
                        </div>
                        <BatteryIndicator level={sensor.battery} status={sensor.batteryStatus as 'GOOD' | 'LOW' | 'CRITICAL'} />
                      </div>
                      <div className="flex justify-between text-sm">
                        <span className="text-muted-foreground">Last Communication:</span>
                        <span className="font-medium text-xs">{formatDateTime(sensor.lastCommunication)}</span>
                      </div>
                      {(sensor.batteryLow || sensor.batteryCritical || !sensor.online) && (
                        <div className="pt-3 border-t">
                          {sensor.batteryCritical && (
                            <Badge variant="outline" className="bg-red-50 text-red-700 border-red-200 mb-2">
                              ⚠️ Critical Battery
                            </Badge>
                          )}
                          {sensor.batteryLow && !sensor.batteryCritical && (
                            <Badge variant="outline" className="bg-yellow-50 text-yellow-700 border-yellow-200 mb-2">
                              ⚠️ Low Battery
                            </Badge>
                          )}
                          {!sensor.online && (
                            <Badge variant="outline" className="bg-gray-50 text-gray-700 border-gray-200">
                              ⚠️ Offline
                            </Badge>
                          )}
                        </div>
                      )}
                      <div className="pt-3 flex justify-end">
                        <Button variant="outline" size="sm" asChild>
                          <Link href={`/dashboard/equipment/sensors/${sensor.id}`}>View Details</Link>
                        </Button>
                      </div>
                    </CardContent>
                  </Card>
                ))}
              </div>

              {/* Pagination */}
              {sensorsData.totalPages > 1 && (
                <div className="flex items-center justify-center gap-2">
                  <Button
                    variant="outline"
                    onClick={() => setSensorPage(sensorPage - 1)}
                    disabled={sensorsData.first}
                  >
                    Previous
                  </Button>
                  <span className="text-sm text-muted-foreground">
                    Page {sensorPage + 1} of {sensorsData.totalPages}
                  </span>
                  <Button
                    variant="outline"
                    onClick={() => setSensorPage(sensorPage + 1)}
                    disabled={sensorsData.last}
                  >
                    Next
                  </Button>
                </div>
              )}
            </>
          ) : (
            <div className="text-center py-12">
              <Activity className="w-12 h-12 mx-auto text-muted-foreground mb-4" />
              <p className="text-muted-foreground">No sensors found</p>
            </div>
          )}
        </TabsContent>
      </Tabs>
    </div>
  );
}
