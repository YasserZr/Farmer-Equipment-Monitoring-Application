'use client';

import { useDashboardStatistics } from '@/hooks/use-events';
import { useFarmerStatistics } from '@/hooks/use-farmers';
import { Activity, AlertTriangle, CheckCircle, Users, Droplet, TrendingUp } from 'lucide-react';

export default function DashboardPage() {
  const { data: eventStats, isLoading: eventsLoading } = useDashboardStatistics();
  const { data: farmerStats, isLoading: farmersLoading } = useFarmerStatistics();

  const stats = [
    {
      name: 'Total Farmers',
      value: farmerStats?.totalFarmers || 0,
      icon: Users,
      color: 'text-blue-600',
      bgColor: 'bg-blue-100',
      change: '+12%',
      changeType: 'increase',
    },
    {
      name: 'Active Equipment',
      value: eventStats?.totalEvents || 0,
      icon: Droplet,
      color: 'text-green-600',
      bgColor: 'bg-green-100',
      change: '+8%',
      changeType: 'increase',
    },
    {
      name: 'Critical Alerts',
      value: eventStats?.criticalEvents || 0,
      icon: AlertTriangle,
      color: 'text-red-600',
      bgColor: 'bg-red-100',
      change: '-4%',
      changeType: 'decrease',
    },
    {
      name: 'Resolved Events',
      value: eventStats?.acknowledgedEvents || 0,
      icon: CheckCircle,
      color: 'text-primary-600',
      bgColor: 'bg-primary-100',
      change: '+16%',
      changeType: 'increase',
    },
  ];

  if (eventsLoading || farmersLoading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600"></div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div>
        <h1 className="text-3xl font-bold text-gray-900">Dashboard</h1>
        <p className="mt-2 text-gray-600">Welcome back! Here's an overview of your equipment monitoring system.</p>
      </div>

      {/* Stats Grid */}
      <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-4">
        {stats.map((stat) => (
          <div key={stat.name} className="bg-white rounded-lg shadow p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-gray-600">{stat.name}</p>
                <p className="mt-2 text-3xl font-semibold text-gray-900">{stat.value}</p>
              </div>
              <div className={`p-3 rounded-lg ${stat.bgColor}`}>
                <stat.icon className={`w-6 h-6 ${stat.color}`} />
              </div>
            </div>
            <div className="mt-4 flex items-center text-sm">
              <TrendingUp
                className={`w-4 h-4 mr-1 ${
                  stat.changeType === 'increase' ? 'text-green-600' : 'text-red-600'
                }`}
              />
              <span
                className={`font-medium ${
                  stat.changeType === 'increase' ? 'text-green-600' : 'text-red-600'
                }`}
              >
                {stat.change}
              </span>
              <span className="ml-1 text-gray-600">from last month</span>
            </div>
          </div>
        ))}
      </div>

      {/* Recent Events */}
      <div className="bg-white rounded-lg shadow">
        <div className="px-6 py-4 border-b border-gray-200">
          <h2 className="text-lg font-semibold text-gray-900">Recent Events</h2>
        </div>
        <div className="p-6">
          {eventStats?.recentEvents && eventStats.recentEvents.length > 0 ? (
            <div className="space-y-4">
              {eventStats.recentEvents.slice(0, 5).map((event) => (
                <div key={event.id} className="flex items-start space-x-4 p-4 border border-gray-200 rounded-lg">
                  <div
                    className={`flex-shrink-0 w-2 h-2 mt-2 rounded-full ${
                      event.severity === 'CRITICAL'
                        ? 'bg-red-500'
                        : event.severity === 'WARNING'
                        ? 'bg-yellow-500'
                        : 'bg-blue-500'
                    }`}
                  ></div>
                  <div className="flex-1 min-w-0">
                    <p className="text-sm font-medium text-gray-900">{event.message}</p>
                    <p className="text-sm text-gray-600 mt-1">Equipment ID: {event.equipmentId}</p>
                    <p className="text-xs text-gray-500 mt-1">
                      {new Date(event.timestamp).toLocaleString()}
                    </p>
                  </div>
                  <span
                    className={`flex-shrink-0 inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
                      event.severity === 'CRITICAL'
                        ? 'bg-red-100 text-red-800'
                        : event.severity === 'WARNING'
                        ? 'bg-yellow-100 text-yellow-800'
                        : 'bg-blue-100 text-blue-800'
                    }`}
                  >
                    {event.severity}
                  </span>
                </div>
              ))}
            </div>
          ) : (
            <p className="text-center text-gray-500 py-8">No recent events</p>
          )}
        </div>
      </div>

      {/* Event Statistics */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="bg-white rounded-lg shadow p-6">
          <h3 className="text-lg font-semibold text-gray-900 mb-4">Events by Type</h3>
          <div className="space-y-3">
            {eventStats?.eventsByType &&
              Object.entries(eventStats.eventsByType).map(([type, count]) => (
                <div key={type} className="flex items-center justify-between">
                  <span className="text-sm text-gray-600">{type.replace(/_/g, ' ')}</span>
                  <span className="text-sm font-semibold text-gray-900">{count}</span>
                </div>
              ))}
          </div>
        </div>

        <div className="bg-white rounded-lg shadow p-6">
          <h3 className="text-lg font-semibold text-gray-900 mb-4">Events by Severity</h3>
          <div className="space-y-3">
            {eventStats?.eventsBySeverity &&
              Object.entries(eventStats.eventsBySeverity).map(([severity, count]) => (
                <div key={severity} className="flex items-center justify-between">
                  <div className="flex items-center">
                    <div
                      className={`w-3 h-3 rounded-full mr-2 ${
                        severity === 'CRITICAL'
                          ? 'bg-red-500'
                          : severity === 'WARNING'
                          ? 'bg-yellow-500'
                          : 'bg-blue-500'
                      }`}
                    ></div>
                    <span className="text-sm text-gray-600">{severity}</span>
                  </div>
                  <span className="text-sm font-semibold text-gray-900">{count}</span>
                </div>
              ))}
          </div>
        </div>
      </div>
    </div>
  );
}
