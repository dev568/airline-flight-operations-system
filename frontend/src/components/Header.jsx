import { Bell, Search, Menu } from 'lucide-react';
import './Header.css';

export function Header() {
  return (
    <header className="header">
      <div className="header-left">
        <button className="menu-button">
          <Menu className="menu-icon" />
        </button>
        <div className="search-bar">
          <Search className="search-icon" />
          <input type="text" placeholder="Search flights, crew, operations..." />
        </div>
      </div>
      <div className="header-right">
        <button className="icon-button">
          <Bell className="bell-icon" />
        </button>
        <div className="user-profile">
          <div className="avatar">A</div>
          <span className="user-name">Admin</span>
        </div>
      </div>
    </header>
  );
}
