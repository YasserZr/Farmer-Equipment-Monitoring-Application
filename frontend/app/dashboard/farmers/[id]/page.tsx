import { Suspense } from 'react';
import { notFound } from 'next/navigation';
import Link from 'next/link';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Separator } from '@/components/ui/separator';
import { Skeleton } from '@/components/ui/skeleton';
import { ArrowLeft, Edit, Mail, Phone, MapPin, Calendar, User } from 'lucide-react';
import { formatDate, formatDateTime } from '@/lib/utils';

// Server component to fetch farmer data
async function getFarmer(id: string) {
  try {
    const res = await fetch(`${process.env.NEXT_PUBLIC_API_BASE_URL}/api/farmers/${id}`, {
      cache: 'no-store',
    });
    
    if (!res.ok) {
      if (res.status === 404) return null;
      throw new Error('Failed to fetch farmer');
    }
    
    return res.json();
  } catch (error) {
    console.error('Error fetching farmer:', error);
    return null;
  }
}

async function getFarms(farmerId: string) {
  try {
    const res = await fetch(
      `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/farmers/${farmerId}/farms?page=0&size=10`,
      { cache: 'no-store' }
    );
    
    if (!res.ok) throw new Error('Failed to fetch farms');
    
    return res.json();
  } catch (error) {
    console.error('Error fetching farms:', error);
    return { content: [], totalElements: 0 };
  }
}

export default async function FarmerDetailPage({ params }: { params: { id: string } }) {
  const [farmer, farmsData] = await Promise.all([
    getFarmer(params.id),
    getFarms(params.id),
  ]);

  if (!farmer) {
    notFound();
  }

  return (
    <div className="space-y-6 max-w-6xl">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-4">
          <Button variant="ghost" size="sm" asChild>
            <Link href="/dashboard/farmers">
              <ArrowLeft className="w-4 h-4 mr-2" />
              Back to Farmers
            </Link>
          </Button>
        </div>
        <Button asChild>
          <Link href={`/dashboard/farmers/${params.id}/edit`}>
            <Edit className="w-4 h-4 mr-2" />
            Edit Farmer
          </Link>
        </Button>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Main Info Card */}
        <Card className="lg:col-span-2">
          <CardHeader>
            <div className="flex items-start justify-between">
              <div className="flex items-center gap-4">
                <div className="w-16 h-16 rounded-full bg-primary flex items-center justify-center text-white font-bold text-2xl">
                  {farmer.firstName[0]}{farmer.lastName[0]}
                </div>
                <div>
                  <CardTitle className="text-2xl">
                    {farmer.firstName} {farmer.lastName}
                  </CardTitle>
                  <p className="text-sm text-muted-foreground mt-1">ID: {farmer.id}</p>
                </div>
              </div>
              <Badge variant={farmer.active ? 'default' : 'secondary'}>
                {farmer.active ? 'Active' : 'Inactive'}
              </Badge>
            </div>
          </CardHeader>
          <CardContent className="space-y-6">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div className="flex items-start gap-3">
                <Mail className="w-5 h-5 text-muted-foreground mt-0.5" />
                <div>
                  <p className="text-sm font-medium">Email</p>
                  <p className="text-sm text-muted-foreground">{farmer.email}</p>
                </div>
              </div>

              <div className="flex items-start gap-3">
                <Phone className="w-5 h-5 text-muted-foreground mt-0.5" />
                <div>
                  <p className="text-sm font-medium">Phone</p>
                  <p className="text-sm text-muted-foreground">{farmer.phoneNumber}</p>
                </div>
              </div>

              <div className="flex items-start gap-3">
                <MapPin className="w-5 h-5 text-muted-foreground mt-0.5" />
                <div>
                  <p className="text-sm font-medium">Address</p>
                  <p className="text-sm text-muted-foreground">{farmer.address}</p>
                </div>
              </div>

              <div className="flex items-start gap-3">
                <Calendar className="w-5 h-5 text-muted-foreground mt-0.5" />
                <div>
                  <p className="text-sm font-medium">Date of Birth</p>
                  <p className="text-sm text-muted-foreground">{formatDate(farmer.dateOfBirth)}</p>
                </div>
              </div>
            </div>

            <Separator />

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <p className="text-sm font-medium mb-1">Registration Date</p>
                <p className="text-sm text-muted-foreground">
                  {formatDateTime(farmer.registrationDate)}
                </p>
              </div>
              {farmer.lastModified && (
                <div>
                  <p className="text-sm font-medium mb-1">Last Modified</p>
                  <p className="text-sm text-muted-foreground">
                    {formatDateTime(farmer.lastModified)}
                  </p>
                </div>
              )}
            </div>
          </CardContent>
        </Card>

        {/* Stats Card */}
        <Card>
          <CardHeader>
            <CardTitle>Statistics</CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            <div className="flex items-center justify-between">
              <span className="text-sm text-muted-foreground">Total Farms</span>
              <span className="text-2xl font-bold">{farmsData.totalElements}</span>
            </div>
            <Separator />
            <div className="flex items-center justify-between">
              <span className="text-sm text-muted-foreground">Account Status</span>
              <Badge variant={farmer.active ? 'default' : 'secondary'}>
                {farmer.active ? 'Active' : 'Inactive'}
              </Badge>
            </div>
          </CardContent>
        </Card>
      </div>

      {/* Farms List */}
      <Card>
        <CardHeader>
          <div className="flex items-center justify-between">
            <CardTitle>Farms ({farmsData.totalElements})</CardTitle>
            <Button size="sm" variant="outline" asChild>
              <Link href={`/dashboard/farmers/${params.id}/farms/new`}>Add Farm</Link>
            </Button>
          </div>
        </CardHeader>
        <CardContent>
          {farmsData.content.length === 0 ? (
            <div className="text-center py-8 text-muted-foreground">
              No farms registered yet
            </div>
          ) : (
            <div className="space-y-4">
              {farmsData.content.map((farm: any) => (
                <div
                  key={farm.id}
                  className="flex items-center justify-between p-4 border rounded-lg hover:bg-muted/50 transition-colors"
                >
                  <div className="flex-1">
                    <h4 className="font-medium">{farm.name}</h4>
                    <div className="flex items-center gap-4 mt-1 text-sm text-muted-foreground">
                      <span className="flex items-center gap-1">
                        <MapPin className="w-3 h-3" />
                        {farm.location}
                      </span>
                      <span>{farm.size} hectares</span>
                    </div>
                  </div>
                  <Button variant="ghost" size="sm" asChild>
                    <Link href={`/dashboard/farms/${farm.id}`}>View</Link>
                  </Button>
                </div>
              ))}
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
}
