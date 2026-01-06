'use client';

import { useState } from 'react';
import Link from 'next/link';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { formatDate, getInitials } from '@/lib/utils';
import { Eye, Edit, Trash2, ChevronLeft, ChevronRight } from 'lucide-react';
import type { Farmer } from '@/types/farmer';
import type { PaginatedResponse } from '@/types/common';

interface FarmersDataTableProps {
  data: PaginatedResponse<Farmer>;
  onDelete?: (id: string) => void;
  onPageChange?: (page: number) => void;
}

export function FarmersDataTable({ data, onDelete, onPageChange }: FarmersDataTableProps) {
  const [currentPage, setCurrentPage] = useState(data.number);

  const handlePageChange = (newPage: number) => {
    setCurrentPage(newPage);
    onPageChange?.(newPage);
  };

  return (
    <div className="space-y-4">
      <div className="rounded-md border">
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>Farmer</TableHead>
              <TableHead>Email</TableHead>
              <TableHead>Phone</TableHead>
              <TableHead>Registration Date</TableHead>
              <TableHead>Status</TableHead>
              <TableHead className="text-right">Actions</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {data.content.length === 0 ? (
              <TableRow>
                <TableCell colSpan={6} className="text-center text-muted-foreground py-8">
                  No farmers found
                </TableCell>
              </TableRow>
            ) : (
              data.content.map((farmer) => (
                <TableRow key={farmer.id} className="hover:bg-muted/50">
                  <TableCell>
                    <div className="flex items-center space-x-3">
                      <div className="w-10 h-10 rounded-full bg-primary flex items-center justify-center text-white font-semibold">
                        {getInitials(farmer.firstName, farmer.lastName)}
                      </div>
                      <div>
                        <div className="font-medium">
                          {farmer.firstName} {farmer.lastName}
                        </div>
                        <div className="text-sm text-muted-foreground">ID: {farmer.id}</div>
                      </div>
                    </div>
                  </TableCell>
                  <TableCell>{farmer.email}</TableCell>
                  <TableCell>{farmer.phoneNumber}</TableCell>
                  <TableCell>{formatDate(farmer.registrationDate)}</TableCell>
                  <TableCell>
                    <Badge variant={farmer.active ? 'default' : 'secondary'}>
                      {farmer.active ? 'Active' : 'Inactive'}
                    </Badge>
                  </TableCell>
                  <TableCell className="text-right">
                    <div className="flex items-center justify-end space-x-2">
                      <Button variant="ghost" size="sm" asChild>
                        <Link href={`/dashboard/farmers/${farmer.id}`}>
                          <Eye className="w-4 h-4" />
                        </Link>
                      </Button>
                      <Button variant="ghost" size="sm" asChild>
                        <Link href={`/dashboard/farmers/${farmer.id}/edit`}>
                          <Edit className="w-4 h-4" />
                        </Link>
                      </Button>
                      {onDelete && (
                        <Button
                          variant="ghost"
                          size="sm"
                          onClick={() => onDelete(farmer.id)}
                          className="text-destructive hover:text-destructive"
                        >
                          <Trash2 className="w-4 h-4" />
                        </Button>
                      )}
                    </div>
                  </TableCell>
                </TableRow>
              ))
            )}
          </TableBody>
        </Table>
      </div>

      {/* Pagination */}
      {data.totalPages > 1 && (
        <div className="flex items-center justify-between px-2">
          <div className="text-sm text-muted-foreground">
            Showing page {currentPage + 1} of {data.totalPages} ({data.totalElements} total farmers)
          </div>
          <div className="flex items-center space-x-2">
            <Button
              variant="outline"
              size="sm"
              onClick={() => handlePageChange(currentPage - 1)}
              disabled={data.first}
            >
              <ChevronLeft className="w-4 h-4 mr-1" />
              Previous
            </Button>
            <Button
              variant="outline"
              size="sm"
              onClick={() => handlePageChange(currentPage + 1)}
              disabled={data.last}
            >
              Next
              <ChevronRight className="w-4 h-4 ml-1" />
            </Button>
          </div>
        </div>
      )}
    </div>
  );
}
