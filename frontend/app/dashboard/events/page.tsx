'use client';

import { useState } from 'react';
import { useEvents, useAcknowledgeEvent } from '@/hooks/use-events';
import { formatDateTime, formatRelativeTime } from '@/lib/utils';
import { CheckCircle, AlertTriangle, Info, Filter } from 'lucide-react';

export default function EventsPage() {
  const [page, setPage] = useState(0);
  const { data, isLoading } = useEvents(page, 20);
  const acknowledgeEvent = useAcknowledgeEvent();

  const handleAcknowledge = async (id: string) => {
    try {
      await acknowledgeEvent.mutateAsync({
        id,
        data: { 
          acknowledgedBy: 'current-user',
          notes: 'Acknowledged from dashboard' 
        },
      });
    } catch (error) {
      // Error handled by hook
    }
  };

  const getSeverityColor = (severity: string) => {
    switch (severity) {
      case 'CRITICAL':
        return 'bg-red-100 text-red-800 border-red-200';
      case 'WARNING':
        return 'bg-yellow-100 text-yellow-800 border-yellow-200';
      default:
        return 'bg-blue-100 text-blue-800 border-blue-200';
    }
  };

  const getSeverityIcon = (severity: string) => {
    switch (severity) {
      case 'CRITICAL':
        return <AlertTriangle className="w-5 h-5 text-red-600" />;
      case 'WARNING':
        return <AlertTriangle className="w-5 h-5 text-yellow-600" />;
      default:
        return <Info className="w-5 h-5 text-blue-600" />;
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
          <h1 className="text-3xl font-bold text-gray-900">Events</h1>
          <p className="mt-2 text-gray-600">Monitor equipment events and alerts in real-time</p>
        </div>
        <button className="flex items-center px-4 py-2 bg-white border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors">
          <Filter className="w-5 h-5 mr-2" />
          Filter Events
        </button>
      </div>

      {/* Events List */}
      <div className="space-y-4">
        {data?.content && data.content.length > 0 ? (
          data.content.map((event) => (
          <div
            key={event.id}
            className={`bg-white rounded-lg shadow-md p-6 border-l-4 ${
              event.acknowledged ? 'opacity-75' : ''
            } ${
              event.severity === 'CRITICAL'
                ? 'border-red-500'
                : event.severity === 'WARNING'
                ? 'border-yellow-500'
                : 'border-blue-500'
            }`}
          >
            <div className="flex items-start justify-between">
              <div className="flex items-start space-x-4 flex-1">
                <div className="flex-shrink-0 mt-1">{getSeverityIcon(event.severity)}</div>
                <div className="flex-1 min-w-0">
                  <div className="flex items-center space-x-3 mb-2">
                    <h3 className="text-lg font-semibold text-gray-900">{event.message}</h3>
                    <span
                      className={`inline-flex px-2.5 py-0.5 rounded-full text-xs font-medium ${getSeverityColor(
                        event.severity
                      )}`}
                    >
                      {event.severity}
                    </span>
                    <span className="inline-flex px-2.5 py-0.5 rounded-full text-xs font-medium bg-gray-100 text-gray-800">
                      {event.eventType.replace(/_/g, ' ')}
                    </span>
                  </div>

                  <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mt-3 text-sm">
                    <div>
                      <span className="text-gray-600">Equipment ID:</span>
                      <p className="font-medium text-gray-900">{event.equipmentId}</p>
                    </div>
                    <div>
                      <span className="text-gray-600">Type:</span>
                      <p className="font-medium text-gray-900">{event.equipmentType}</p>
                    </div>
                    <div>
                      <span className="text-gray-600">Farm ID:</span>
                      <p className="font-medium text-gray-900">{event.farmId}</p>
                    </div>
                    <div>
                      <span className="text-gray-600">Time:</span>
                      <p className="font-medium text-gray-900" title={formatDateTime(event.timestamp)}>
                        {formatRelativeTime(event.timestamp)}
                      </p>
                    </div>
                  </div>

                  {event.payload && Object.keys(event.payload).length > 0 && (
                    <div className="mt-3 p-3 bg-gray-50 rounded-lg">
                      <p className="text-xs font-medium text-gray-700 mb-1">Event Details:</p>
                      <pre className="text-xs text-gray-600 overflow-x-auto">
                        {JSON.stringify(event.payload, null, 2)}
                      </pre>
                    </div>
                  )}

                  {event.acknowledged && event.acknowledgedBy && (
                    <div className="mt-3 flex items-center text-sm text-gray-600">
                      <CheckCircle className="w-4 h-4 mr-2 text-green-600" />
                      <span>
                        Acknowledged by {event.acknowledgedBy} on {formatDateTime(event.acknowledgedAt!)}
                      </span>
                    </div>
                  )}
                </div>
              </div>

              <div className="flex-shrink-0 ml-4">
                {!event.acknowledged && (
                  <button
                    onClick={() => handleAcknowledge(event.id)}
                    disabled={acknowledgeEvent.isPending}
                    className="px-4 py-2 bg-primary-600 text-white text-sm font-medium rounded-lg hover:bg-primary-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                  >
                    Acknowledge
                  </button>
                )}
              </div>
            </div>
          </div>
        ))
        ) : (
          <div className="text-center py-12 bg-white rounded-lg shadow">
            <Info className="w-12 h-12 text-gray-400 mx-auto mb-4" />
            <p className="text-gray-600">No events found</p>
          </div>
        )}
      </div>

      {/* Pagination */}
      {data && data.totalPages > 1 && (
        <div className="flex items-center justify-between bg-white px-6 py-3 rounded-lg shadow">
          <div className="text-sm text-gray-700">
            Showing page {page + 1} of {data.totalPages} ({data.totalElements} total events)
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
