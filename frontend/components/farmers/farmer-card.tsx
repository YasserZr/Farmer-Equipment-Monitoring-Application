'use client';

import Link from 'next/link';
import { Card, CardContent, CardFooter, CardHeader } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { formatDate, getInitials } from '@/lib/utils';
import { Eye, Edit, Trash2, Mail, Phone, MapPin } from 'lucide-react';
import type { Farmer } from '@/types/farmer';

interface FarmerCardProps {
  farmer: Farmer;
  onDelete?: (id: string) => void;
}

export function FarmerCard({ farmer, onDelete }: FarmerCardProps) {
  return (
    <Card className="hover:shadow-lg transition-shadow">
      <CardHeader className="space-y-0 pb-4">
        <div className="flex items-start justify-between">
          <div className="flex items-center space-x-3">
            <div className="w-12 h-12 rounded-full bg-primary flex items-center justify-center text-white font-semibold text-lg">
              {getInitials(farmer.firstName, farmer.lastName)}
            </div>
            <div>
              <h3 className="text-lg font-semibold">
                {farmer.firstName} {farmer.lastName}
              </h3>
              <p className="text-sm text-muted-foreground">ID: {farmer.id}</p>
            </div>
          </div>
          <Badge variant={farmer.active ? 'default' : 'secondary'}>
            {farmer.active ? 'Active' : 'Inactive'}
          </Badge>
        </div>
      </CardHeader>

      <CardContent className="space-y-2">
        <div className="flex items-center space-x-2 text-sm">
          <Mail className="w-4 h-4 text-muted-foreground" />
          <span className="text-muted-foreground">{farmer.email}</span>
        </div>
        <div className="flex items-center space-x-2 text-sm">
          <Phone className="w-4 h-4 text-muted-foreground" />
          <span className="text-muted-foreground">{farmer.phoneNumber}</span>
        </div>
        <div className="flex items-center space-x-2 text-sm">
          <MapPin className="w-4 h-4 text-muted-foreground" />
          <span className="text-muted-foreground truncate">{farmer.address}</span>
        </div>
        <div className="pt-2 text-xs text-muted-foreground">
          Registered: {formatDate(farmer.registrationDate)}
        </div>
      </CardContent>

      <CardFooter className="flex justify-end space-x-2 pt-4 border-t">
        <Button variant="ghost" size="sm" asChild>
          <Link href={`/dashboard/farmers/${farmer.id}`}>
            <Eye className="w-4 h-4 mr-1" />
            View
          </Link>
        </Button>
        <Button variant="ghost" size="sm" asChild>
          <Link href={`/dashboard/farmers/${farmer.id}/edit`}>
            <Edit className="w-4 h-4 mr-1" />
            Edit
          </Link>
        </Button>
        {onDelete && (
          <Button
            variant="ghost"
            size="sm"
            onClick={() => onDelete(farmer.id)}
            className="text-destructive hover:text-destructive"
          >
            <Trash2 className="w-4 h-4 mr-1" />
            Delete
          </Button>
        )}
      </CardFooter>
    </Card>
  );
}
