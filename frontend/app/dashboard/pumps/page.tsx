'use client';

import { useState } from 'react';
import { usePumps, useDeletePump } from '@/hooks/use-equipment';
import { formatDate } from '@/lib/utils';
import { Plus, Edit, Trash2, Wrench, CheckCircle, XCircle } from 'lucide-react';

export default function PumpsPage() {
  const [page, setPage] = useState(0);
  const { data, isLoading } = usePumps(page, 10);
  const deletePump = useDeletePump();

  const handleDelete = async (id: string) => {
    if (confirm('Are you sure you want to delete this pump?')) {
      try {
        await deletePump.mutateAsync(id);
      } catch (error) {
        // Error handled by hook
      }
    }
  };

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600"></div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Pumps</h1>
          <p className="mt-2 text-gray-600">Monitor and manage irrigation pumps</p>
        </div>
        <button className="flex items-center px-4 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700 transition-colors">
          <Plus className="w-5 h-5 mr-2" />
          Add Pump
        </button>
      </div>

      {/* Grid View */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {data?.content.map((pump) => (
          <div key={pump.id} className="bg-white rounded-lg shadow-md p-6 hover:shadow-lg transition-shadow">
            <div className="flex items-start justify-between mb-4">
              <div className="flex items-center">
                <div className="w-12 h-12 bg-blue-100 rounded-lg flex items-center justify-center">
                  <CheckCircle className={`w-6 h-6 ${pump.operational ? 'text-green-600' : 'text-red-600'}`} />
                </div>
                <div className="ml-3">
                  <h3 className="text-lg font-semibold text-gray-900">{pump.model}</h3>
                  <p className="text-sm text-gray-500">ID: {pump.id}</p>
                </div>
              </div>
              <span
                className={`inline-flex px-2.5 py-0.5 rounded-full text-xs font-medium ${
                  pump.status === 'ACTIVE'
                    ? 'bg-green-100 text-green-800'
                    : pump.status === 'MAINTENANCE'
                    ? 'bg-yellow-100 text-yellow-800'
                    : 'bg-red-100 text-red-800'
                }`}
              >
                {pump.status}
              </span>
            </div>

            <div className="space-y-2 mb-4">
              <div className="flex justify-between text-sm">
                <span className="text-gray-600">Location:</span>
                <span className="font-medium text-gray-900">{pump.location}</span>
              </div>
              <div className="flex justify-between text-sm">
                <span className="text-gray-600">Max Flow:</span>
                <span className="font-medium text-gray-900">{pump.formattedMaxFlow}</span>
              </div>
              <div className="flex justify-between text-sm">
                <span className="text-gray-600">Installed:</span>
                <span className="font-medium text-gray-900">{formatDate(pump.installationDate)}</span>
              </div>
              {pump.nextMaintenanceDate && (
                <div className="flex justify-between text-sm">
                  <span className="text-gray-600">Next Maintenance:</span>
                  <span className={`font-medium ${pump.maintenanceOverdue ? 'text-red-600' : 'text-gray-900'}`}>
                    {formatDate(pump.nextMaintenanceDate)}
                    {pump.maintenanceOverdue && <span className="ml-1">⚠️</span>}
                  </span>
                </div>
              )}
            </div>

            <div className="flex items-center justify-end space-x-2 pt-4 border-t border-gray-200">
              <button className="p-2 text-blue-600 hover:bg-blue-50 rounded-lg transition-colors">
                <Wrench className="w-5 h-5" />
              </button>
              <button className="p-2 text-primary-600 hover:bg-primary-50 rounded-lg transition-colors">
                <Edit className="w-5 h-5" />
              </button>
              <button
                onClick={() => handleDelete(pump.id)}
                className="p-2 text-red-600 hover:bg-red-50 rounded-lg transition-colors"
              >
                <Trash2 className="w-5 h-5" />
              </button>
            </div>
          </div>
        ))}
      </div>

      {/* Pagination */}
      {data && data.totalPages > 1 && (
        <div className="flex items-center justify-between bg-white px-6 py-3 rounded-lg shadow">
          <div className="text-sm text-gray-700">
            Showing page {page + 1} of {data.totalPages}
          </div>
          <div className="flex space-x-2">
            <button
              onClick={() => setPage(page - 1)}
              disabled={page === 0}
              className="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              Previous
            </button>
            <button
              onClick={() => setPage(page + 1)}
              disabled={page >= data.totalPages - 1}
              className="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              Next
            </button>
          </div>
        </div>
      )}
    </div>
  );
}
