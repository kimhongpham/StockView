import { forwardRef } from 'react';
import { ChevronLeft, ChevronRight } from 'lucide-react';
import { useAuthStore } from '../../store/authStore';

interface SidebarProps {
  activePage: string;
  onPageChange: (page: string) => void;
  isLoggedIn: boolean;
  isActive: boolean;
  isCollapsed: boolean;
  onToggle: () => void;
}

export const Sidebar = forwardRef<HTMLDivElement, SidebarProps>(
  ({ activePage, onPageChange, isLoggedIn, isActive, isCollapsed, onToggle }, ref) => {
    const { user } = useAuthStore();

    const allMenuItems = [
      { id: 'dashboard', icon: 'fas fa-chart-line', label: 'Thị Trường', premium: false, roles: ['guest', 'user', 'admin'] },
      { id: 'stock', icon: 'fas fa-chart-bar', label: 'Danh Mục Quan Sát', premium: false, roles: ['guest', 'user'] },
      { id: 'favorit', icon: 'fas fa-star', label: 'Đang Theo Dõi', premium: false, roles: ['user'] },
      { id: 'wallet', icon: 'fas fa-wallet', label: 'Danh Mục Đầu Tư', premium: true, roles: ['user'] },
      { id: 'profile', icon: 'fas fa-user', label: 'Hồ Sơ', premium: false, roles: ['user'] },
      { id: 'admin', icon: 'fas fa-cog', label: 'Quản Trị', premium: false, roles: ['admin'] },
    ];

    const filteredMenuItems = allMenuItems.filter(item => {
      // If not logged in, only show guest accessible items
      if (!isLoggedIn) {
        return item.roles.includes('guest');
      }

      // Check role access
      const userRole = user?.role?.toLowerCase() || 'user';
      if (!item.roles.includes(userRole)) return false;
      
      // Check premium access
      if (item.premium && !isLoggedIn) return false;
      
      return true;
    });

    return (
      <nav
        ref={ref}
        className={`sidebar ${isActive ? 'active' : ''} ${isCollapsed ? 'collapsed' : ''}`}
        aria-label="Main navigation"
      >
        {/* Logo chỉ hiển thị ở màn hình md trở lên */}
        <div className="sidebar-header hidden md:flex items-center justify-between px-3 py-2">
          <div className="logo flex items-center gap-2">
            <img
              src="/logo.png"
              alt="StockView Logo"
              className="h-8 w-8 object-contain"
            />
            {!isCollapsed && <span className="font-semibold text-lg">StockView</span>}
            {isCollapsed && <span className="font-semibold text-lg">SV</span>}
          </div>
          <button
            className="sidebar-toggle"
            onClick={onToggle}
            aria-label={isCollapsed ? 'Expand sidebar' : 'Collapse sidebar'}
          >
            {isCollapsed ? (
              <ChevronRight className="w-4 h-4" />
            ) : (
              <ChevronLeft className="w-4 h-4" />
            )}
          </button>
        </div>

        {/* Menu chính */}
        <ul className="sidebar-menu">
          {filteredMenuItems.map((item) => (
            <li
              key={item.id}
              className={`menu-item ${activePage === item.id ? 'active' : ''} ${item.premium ? 'premium-feature' : ''}`}
              data-page={item.id}
              onClick={() => onPageChange(item.id)}
              title={isCollapsed ? item.label : undefined}
            >
              <i className={item.icon}></i>
              {!isCollapsed && <span className="menu-label">{item.label}</span>}
              {item.premium && !isCollapsed && <span className="premium-badge">PRO</span>}
            </li>
          ))}
        </ul>

        {!isCollapsed && (
          <div className="sidebar-footer">
            <div className="user-status">
              <div className="status-indicator online"></div>
              <span>Đang hoạt động</span>
            </div>
          </div>
        )}
      </nav>
    );
  }
);
