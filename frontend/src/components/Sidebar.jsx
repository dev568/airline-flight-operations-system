import { Link, useLocation } from 'react-router-dom';
import { Plane, Users, ClipboardList, LayoutDashboard } from 'lucide-react';
import './Sidebar.css';

export function Sidebar() {
  const location = useLocation();

  const menuItems = [
    { path: '/', icon: LayoutDashboard, label: 'Dashboard' },
    { path: '/flights', icon: Plane, label: 'Flights' },
    { path: '/crew', icon: Users, label: 'Crew Members' },
    { path: '/operations', icon: ClipboardList, label: 'Flight Operations' },
  ];

  return (
    <aside className="sidebar">
      <div className="sidebar-header">
        <Plane className="logo-icon" />
        <h1>Airline Ops</h1>
      </div>
      <nav className="sidebar-nav">
        {menuItems.map((item) => {
          const Icon = item.icon;
          const isActive = location.pathname === item.path;
          return (
            <Link
              key={item.path}
              to={item.path}
              className={`nav-item ${isActive ? 'active' : ''}`}
            >
              <Icon className="nav-icon" />
              <span>{item.label}</span>
            </Link>
          );
        })}
      </nav>
    </aside>
  );
}
