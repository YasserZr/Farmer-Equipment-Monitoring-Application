'use client';

import { useState } from 'react';
import Link from 'next/link';
import { useFarmers, useFarms, useCreateFarm, useDeleteFarm } from '@/hooks/use-farmers';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Badge } from '@/components/ui/badge';
import { Skeleton } from '@/components/ui/skeleton';
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
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from '@/components/ui/alert-dialog';
import { CreateFarmForm } from '@/components/farms/create-farm-form';
import { Search, Plus, MapPin, Ruler, User, Trash2, Eye } from 'lucide-react';
import { formatDate } from '@/lib/utils';
import type { CreateFarmRequest } from '@/types/farmer';

export default function FarmsPage() {
  const [searchQuery, setSearchQuery] = useState('');
  const [createDialogOpen, setCreateDialogOpen] = useState(false);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [selectedFarm, setSelectedFarm] = useState<{ id: string; farmerId: string; name: string } | null>(null);

  const { data: farmersData, isLoading: farmersLoading } = useFarmers(0, 100);

  // Get all farms for all farmers
  const farmerIds = farmersData?.content?.map(f => f.id) || [];
  
  const handleCreateFarm = async (data: CreateFarmRequest) => {
    const createFarm = useCreateFarm(data.farmerId);
    try {
      await createFarm.mutateAsync(data);
      setCreateDialogOpen(false);
    } catch (error) {
      // Error handled by hook
    }
  };

  const handleDeleteFarm = async () => {
    if (!selectedFarm) return;
    const deleteFarm = useDeleteFarm(selectedFarm.farmerId);
    try {
      await deleteFarm.mutateAsync(selectedFarm.id);
      setDeleteDialogOpen(false);
      setSelectedFarm(null);
    } catch (error) {
      // Error handled by hook
    }
  };

  return (
    <div className="space-y-6 max-w-7xl">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold">Farm Management</h1>
          <p className="text-muted-foreground mt-1">
            Manage farms and their locations
          </p>
        </div>
        <Dialog open={createDialogOpen} onOpenChange={setCreateDialogOpen}>
          <DialogTrigger asChild>
            <Button>
              <Plus className="w-4 h-4 mr-2" />
              Add Farm
            </Button>
          </DialogTrigger>
          <DialogContent className="max-w-2xl">
            <DialogHeader>
              <DialogTitle>Create New Farm</DialogTitle>
              <DialogDescription>
                Add a new farm to the system
              </DialogDescription>
            </DialogHeader>
            <CreateFarmForm onSubmit={handleCreateFarm} />
          </DialogContent>
        </Dialog>
      </div>

      {/* Search and Filters */}
      <Card>
        <CardContent className="pt-6">
          <div className="relative">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground" />
            <Input
              placeholder="Search farms by name, location, or owner..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="pl-10"
            />
          </div>
        </CardContent>
      </Card>

      {/* Farms List */}
      <Card>
        <CardHeader>
          <CardTitle>All Farms</CardTitle>
          <CardDescription>
            Complete list of farms in the system
          </CardDescription>
        </CardHeader>
        <CardContent>
          {farmersLoading ? (
            <div className="space-y-2">
              {[...Array(5)].map((_, i) => (
                <Skeleton key={i} className="h-16 w-full" />
              ))}
            </div>
          ) : farmersData && farmersData.content && farmersData.content.length > 0 ? (
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Farm Name</TableHead>
                  <TableHead>Location</TableHead>
                  <TableHead>Size</TableHead>
                  <TableHead>Owner</TableHead>
                  <TableHead>Created Date</TableHead>
                  <TableHead className="text-right">Actions</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {farmersData.content
                  .filter(farmer => 
                    farmer.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
                    farmer.email.toLowerCase().includes(searchQuery.toLowerCase())
                  )
                  .flatMap((farmer) => 
                    // For demo purposes, show farmer as farm owner
                    [{
                      id: farmer.id,
                      farmerId: farmer.id,
                      name: `${farmer.name}'s Farm`,
                      location: 'Not specified',
                      size: 0,
                      createdAt: farmer.createdAt,
                      ownerName: farmer.name,
                      ownerEmail: farmer.email,
                    }]
                  )
                  .map((farm) => (
                    <TableRow key={farm.id}>
                      <TableCell className="font-medium">
                        <div className="flex items-center gap-2">
                          <MapPin className="w-4 h-4 text-muted-foreground" />
                          {farm.name}
                        </div>
                      </TableCell>
                      <TableCell>{farm.location}</TableCell>
                      <TableCell>
                        <div className="flex items-center gap-1">
                          <Ruler className="w-4 h-4 text-muted-foreground" />
                          {farm.size > 0 ? `${farm.size} acres` : 'N/A'}
                        </div>
                      </TableCell>
                      <TableCell>
                        <div className="flex items-center gap-2">
                          <User className="w-4 h-4 text-muted-foreground" />
                          <div>
                            <p className="font-medium text-sm">{farm.ownerName}</p>
                            <p className="text-xs text-muted-foreground">{farm.ownerEmail}</p>
                          </div>
                        </div>
                      </TableCell>
                      <TableCell>{formatDate(farm.createdAt)}</TableCell>
                      <TableCell className="text-right">
                        <div className="flex items-center justify-end gap-2">
                          <Button variant="ghost" size="sm" asChild>
                            <Link href={`/dashboard/farms/${farm.id}`}>
                              <Eye className="w-4 h-4" />
                            </Link>
                          </Button>
                          <Button
                            variant="ghost"
                            size="sm"
                            onClick={() => {
                              setSelectedFarm({
                                id: farm.id,
                                farmerId: farm.farmerId,
                                name: farm.name,
                              });
                              setDeleteDialogOpen(true);
                            }}
                          >
                            <Trash2 className="w-4 h-4 text-red-600" />
                          </Button>
                        </div>
                      </TableCell>
                    </TableRow>
                  ))}
              </TableBody>
            </Table>
          ) : (
            <div className="text-center py-12">
              <MapPin className="w-12 h-12 mx-auto text-muted-foreground mb-4" />
              <p className="text-muted-foreground mb-4">No farms found</p>
              <Button variant="outline" onClick={() => setCreateDialogOpen(true)}>
                <Plus className="w-4 h-4 mr-2" />
                Create Your First Farm
              </Button>
            </div>
          )}
        </CardContent>
      </Card>

      {/* Delete Confirmation Dialog */}
      <AlertDialog open={deleteDialogOpen} onOpenChange={setDeleteDialogOpen}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Are you sure?</AlertDialogTitle>
            <AlertDialogDescription>
              This will permanently delete the farm "{selectedFarm?.name}". This action cannot be undone.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel onClick={() => setSelectedFarm(null)}>
              Cancel
            </AlertDialogCancel>
            <AlertDialogAction onClick={handleDeleteFarm} className="bg-red-600 hover:bg-red-700">
              Delete Farm
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </div>
  );
}
