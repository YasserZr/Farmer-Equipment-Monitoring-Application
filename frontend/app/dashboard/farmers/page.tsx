'use client';

import { useState } from 'react';
import Link from 'next/link';
import { useFarmers, useDeleteFarmer } from '@/hooks/use-farmers';
import { FarmersDataTable } from '@/components/farmers/farmers-data-table';
import { FarmerCard } from '@/components/farmers/farmer-card';
import { DeleteFarmerDialog } from '@/components/farmers/delete-farmer-dialog';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Skeleton } from '@/components/ui/skeleton';
import { Plus, Search, Grid, List } from 'lucide-react';
import { toast } from 'sonner';

export default function FarmersPage() {
  const [page, setPage] = useState(0);
  const [search, setSearch] = useState('');
  const [viewMode, setViewMode] = useState<'grid' | 'table'>('table');
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [farmerToDelete, setFarmerToDelete] = useState<{ id: string; name: string } | null>(null);

  const { data, isLoading, isError } = useFarmers(page, 10);
  const deleteFarmer = useDeleteFarmer();

  const handleDeleteClick = (id: string, name: string) => {
    setFarmerToDelete({ id, name });
    setDeleteDialogOpen(true);
  };

  const handleDeleteConfirm = async () => {
    if (!farmerToDelete) return;

    try {
      await deleteFarmer.mutateAsync(farmerToDelete.id);
      setDeleteDialogOpen(false);
      setFarmerToDelete(null);
    } catch (error) {
      // Error handled by hook
    }
  };

  if (isError) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="text-center">
          <p className="text-destructive mb-2">Failed to load farmers</p>
          <Button onClick={() => window.location.reload()}>Retry</Button>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold">Farmers</h1>
          <p className="mt-2 text-muted-foreground">
            Manage farmer accounts and their information
          </p>
        </div>
        <Button asChild>
          <Link href="/dashboard/farmers/new">
            <Plus className="w-4 h-4 mr-2" />
            Add Farmer
          </Link>
        </Button>
      </div>

      {/* Search and View Controls */}
      <div className="flex items-center justify-between gap-4">
        <div className="relative flex-1 max-w-md">
          <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-muted-foreground" />
          <Input
            placeholder="Search farmers..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            className="pl-10"
          />
        </div>
        <div className="flex items-center gap-2">
          <Button
            variant={viewMode === 'table' ? 'default' : 'outline'}
            size="sm"
            onClick={() => setViewMode('table')}
          >
            <List className="w-4 h-4" />
          </Button>
          <Button
            variant={viewMode === 'grid' ? 'default' : 'outline'}
            size="sm"
            onClick={() => setViewMode('grid')}
          >
            <Grid className="w-4 h-4" />
          </Button>
        </div>
      </div>

      {/* Loading State */}
      {isLoading && (
        <div className="space-y-4">
          {Array.from({ length: 5 }).map((_, i) => (
            <Skeleton key={i} className="h-20 w-full" />
          ))}
        </div>
      )}

      {/* Content */}
      {!isLoading && data && data.content && (
        <>
          {viewMode === 'table' ? (
            <FarmersDataTable
              data={data}
              onDelete={(id) => {
                const farmer = data.content?.find((f) => f.id === id);
                if (farmer) {
                  handleDeleteClick(id, farmer.name);
                }
              }}
              onPageChange={setPage}
            />
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {data.content?.map((farmer) => (
                <FarmerCard
                  key={farmer.id}
                  farmer={farmer}
                  onDelete={(id) =>
                    handleDeleteClick(id, farmer.name)
                  }
                />
              )) || null}
            </div>
          )}
        </>
      )}

      {/* Delete Dialog */}
      <DeleteFarmerDialog
        open={deleteDialogOpen}
        onOpenChange={setDeleteDialogOpen}
        onConfirm={handleDeleteConfirm}
        farmerName={farmerToDelete?.name}
      />
    </div>
  );
}
