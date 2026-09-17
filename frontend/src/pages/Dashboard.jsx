import { useState, useEffect } from 'react';
import { Card } from '../components/Card';
import { Button } from '../components/Button';
import { Plane, Users, ClipboardList, Activity, ArrowUp, ArrowDown } from 'lucide-react';
import { flightService } from '../services/flightService';
import { crewService } from '../services/crewService';
import { operationService } from '../services/operationService';
import './Dashboard.css';
import './Page.css';

export function Dashboard() {
  const [stats, setStats] = useState({
    totalFlights: 0,
    totalCrew: 0,
    totalOperations: 0,
    healthStatus: 'unknown',
  });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    loadDashboardData();
  }, []);

  const loadDashboardData = async () => {
    try {
      setLoading(true);
      setError(null);

      const [flightsResponse, crewResponse, operationsResponse] = await Promise.all([
        flightService.getAllFlights().catch(err => {
          console.error('Failed to load flights:', err);
          return { data: [] };
        }),
        crewService.getAllCrewMembers().catch(err => {
          console.error('Failed to load crew members:', err);
          return { data: [] };
        }),
        operationService.getAllOperations().catch(err => {
          console.error('Failed to load operations:', err);
          return { data: [] };
        }),
      ]);

      setStats({
        totalFlights: flightsResponse.data.length,
        totalCrew: crewResponse.data.length,
        totalOperations: operationsResponse.data.length,
        healthStatus: 'healthy',
      });
    } catch (err) {
      console.error('Dashboard load error:', err);
      setError(err.message);
      setStats((prev) => ({ ...prev, healthStatus: 'unhealthy' }));
    } finally {
      setLoading(false);
    }
  };

  const StatCard = ({ title, value, icon: Icon, trend, trendValue }) => (
    <Card className="stat-card">
      <div className="stat-header">
        <Icon className="stat-icon" />
        <span className="stat-title">{title}</span>
      </div>
      <div className="stat-value">{value !== undefined ? value : 0}</div>
      {trend && (
        <div className={`stat-trend ${trend}`}>
          {trend === 'up' ? <ArrowUp className="trend-icon" /> : <ArrowDown className="trend-icon" />}
          <span>{trendValue}</span>
        </div>
      )}
    </Card>
  );

  if (loading) {
    return (
      <div className="page">
        <div className="page-header">
          <h1>Dashboard</h1>
        </div>
        <div className="loading-state">Loading dashboard data...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="page">
        <div className="page-header">
          <h1>Dashboard</h1>
        </div>
        <div className="error-state">
          <p>Error loading dashboard: {error}</p>
          <Button onClick={loadDashboardData}>Retry</Button>
        </div>
      </div>
    );
  }

  return (
    <div className="page">
      <div className="page-header">
        <h1>Dashboard</h1>
        <Button onClick={loadDashboardData} icon={Activity}>
          Refresh
        </Button>
      </div>

      <div className="stats-grid">
        <StatCard
          title="Total Flights"
          value={stats.totalFlights}
          icon={Plane}
          trend="up"
          trendValue="+12%"
        />
        <StatCard
          title="Crew Members"
          value={stats.totalCrew}
          icon={Users}
          trend="up"
          trendValue="+5%"
        />
        <StatCard
          title="Flight Operations"
          value={stats.totalOperations}
          icon={ClipboardList}
          trend="up"
          trendValue="+8%"
        />
        <StatCard
          title="System Health"
          value={stats.healthStatus === 'healthy' ? 'All Systems Operational' : 'System Issues'}
          icon={Activity}
          trend={stats.healthStatus === 'healthy' ? 'up' : 'down'}
          trendValue={stats.healthStatus === 'healthy' ? '100%' : '0%'}
        />
      </div>

      <div className="dashboard-grid">
        <Card title="Recent Activity" className="activity-card">
          <div className="activity-list">
            <div className="activity-item">
              <div className="activity-icon flight">✈</div>
              <div className="activity-content">
                <p className="activity-text">Flight UA123 scheduled for departure</p>
                <span className="activity-time">2 minutes ago</span>
              </div>
            </div>
            <div className="activity-item">
              <div className="activity-icon crew">👥</div>
              <div className="activity-content">
                <p className="activity-text">Crew member John Smith assigned to flight BA456</p>
                <span className="activity-time">15 minutes ago</span>
              </div>
            </div>
            <div className="activity-item">
              <div className="activity-icon operation">📋</div>
              <div className="activity-content">
                <p className="activity-text">Operation OP-001 status updated to DEPARTED</p>
                <span className="activity-time">1 hour ago</span>
              </div>
            </div>
          </div>
        </Card>

        <Card title="Quick Actions" className="actions-card">
          <div className="quick-actions">
            <Button variant="primary" icon={Plane}>
              Create Flight
            </Button>
            <Button variant="secondary" icon={Users}>
              Add Crew Member
            </Button>
            <Button variant="secondary" icon={ClipboardList}>
              Create Operation
            </Button>
          </div>
        </Card>
      </div>
    </div>
  );
}
