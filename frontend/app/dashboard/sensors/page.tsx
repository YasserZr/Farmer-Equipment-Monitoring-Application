'use client';

import { useState } from 'react';
import { useSensors, useDeleteSensor } from '@/hooks/use-equipment';
import { formatDateTime } from '@/lib/utils';
import { Plus, Edit, Trash2, Activity, Battery, BatteryLow, BatteryWarning } from 'lucide-react';

export default function SensorsPage() {
  const [page, setPage] = useState(0);
  const { data, isLoading } = useSensors(page, 10);
  const deleteSensor = useDeleteSensor();

  const handleDelete = async (id: string) => {
    if (confirm('Are you sure you want to delete this sensor?')) {
      try {
        await deleteSensor.mutateAsync(id);
      } catch (error) {
        // Error handled by hook
      }
    }
  };

  const getBatteryIcon = (level: number) => {
    if (level > 50) return Battery;
    if (level > 20) return BatteryLow;
    return BatteryWarning;
  };

  const getBatteryColor = (status: string) => {
    if (status === 'GOOD') return 'text-green-600';
    if (status === 'LOW') return 'text-yellow-600';
    return 'text-red-600';
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
          <h1 className="text-3xl font-bold text-gray-900">Sensors</h1>
          <p className="mt-2 text-gray-600">Monitor environmental sensors and their status</p>
        </div>
        <button className="flex items-center px-4 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700 transition-colors">
          <Plus className="w-5 h-5 mr-2" />
          Add Sensor
        </button>
      </div>

      {/* Grid View */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {data?.content.map((sensor) => {
          const BatteryIcon = getBatteryIcon(sensor.batteryLevel);
          return (
            <div key={sensor.id} className="bg-white rounded-lg shadow-md p-6 hover:shadow-lg transition-shadow">
              <div className="flex items-start justify-between mb-4">
                <div className="flex items-center">
                  <div className={`w-12 h-12 ${sensor.online ? 'bg-green-100' : 'bg-gray-100'} rounded-lg flex items-center justify-center`}>
                    <Activity className={`w-6 h-6 ${sensor.online ? 'text-green-600' : 'text-gray-400'}`} />
                  </div>
                  <div className="ml-3">
                    <h3 className="text-lg font-semibold text-gray-900">{sensor.type}</h3>
                    <p className="text-sm text-gray-500">{sensor.model}</p>
                  </div>
                </div>
                <span
                  className={`inline-flex px-2.5 py-0.5 rounded-full text-xs font-medium ${
                    sensor.active ? 'bg-green-100 text-green-800' : 'bg-gray-100 text-gray-800'
                  }`}
                >
                  {sensor.active ? 'Active' : 'Inactive'}
                </span>
              </div>

              <div className="space-y-2 mb-4">
                <div className="flex justify-between text-sm">
                  <span className="text-gray-600">Location:</span>
                  <span className="font-medium text-gray-900">{sensor.location}</span>
                </div>
                <div className="flex justify-between text-sm">
                  <span className="text-gray-600">Battery:</span>
                  <div className="flex items-center">
                    <BatteryIcon className={`w-4 h-4 mr-1 ${getBatteryColor(sensor.batteryStatus)}`} />
                    <span className={`font-medium ${getBatteryColor(sensor.batteryStatus)}`}>
                      {sensor.batteryLevel}%
                    </span>
                  </div>
                </div>
                <div className="flex justify-between text-sm">
                  <span className="text-gray-600">Status:</span>
                  <span className={`font-medium ${sensor.online ? 'text-green-600' : 'text-red-600'}`}>
                    {sensor.online ? 'Online' : 'Offline'}
                  </span>
                </div>
                <div className="flex justify-between text-sm">
                  <span className="text-gray-600">Last Communication:</span>
                  <span className="font-medium text-gray-900">{formatDateTime(sensor.lastCommunication)}</span>
                </div>
                {sensor.alertThreshold && (
                  <div className="flex justify-between text-sm">
                    <span className="text-gray-600">Alert Threshold:</span>
                    <span className="font-medium text-gray-900">
                      {sensor.alertThreshold} {sensor.unit}
                    </span>
                  </div>
                )}
              </div>

              {/* Warnings */}
              {(sensor.batteryLow || sensor.batteryCritical || !sensor.online) && (
                <div className="mb-4 p-3 bg-yellow-50 border border-yellow-200 rounded-lg">
                  <div className="flex items-start space-x-2">
                    <Activity className="w-4 h-4 text-yellow-600 mt-0.5" />
                    <div className="flex-1">
                      {sensor.batteryCritical && (
                        <p className="text-xs text-yellow-800 font-medium">⚠️ Critical battery level</p>
                      )}
                      {sensor.batteryLow && !sensor.batteryCritical && (
                        <p className="text-xs text-yellow-800 font-medium">⚠️ Low battery</p>
                      )}
                      {!sensor.online && (
                        <p className="text-xs text-yellow-800 font-medium">⚠️ Sensor offline</p>
                      )}
                    </div>
                  </div>
                </div>
              )}

              <div className="flex items-center justify-end space-x-2 pt-4 border-t border-gray-200">
                <button className="p-2 text-primary-600 hover:bg-primary-50 rounded-lg transition-colors">
                  <Edit className="w-5 h-5" />
                </button>
                <button
                  onClick={() => handleDelete(sensor.id)}
                  className="p-2 text-red-600 hover:bg-red-50 rounded-lg transition-colors"
                >
                  <Trash2 className="w-5 h-5" />
                </button>
              </div>
            </div>
          );
        })}
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
