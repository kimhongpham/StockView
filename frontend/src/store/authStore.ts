import { create } from 'zustand';

interface User {
  name: string;
  role: string;
  avatar: string;
  email: string;
  token?: string;
}

interface AuthState {
  user: User | null;
  isLoggedIn: boolean;
  login: (user: User) => void;
  logout: () => void;
  initializeAuth: () => void;
}

export const useAuthStore = create<AuthState>((set) => ({
  user: null,
  isLoggedIn: false,
  
  login: (user) => {
    set({ user, isLoggedIn: true });
  },
  
  logout: () => {
    localStorage.removeItem('authToken');
    localStorage.removeItem('user');
    set({ user: null, isLoggedIn: false });
  },

  initializeAuth: () => {
    // Kiểm tra token trong localStorage khi app khởi động
    const token = localStorage.getItem('authToken');
    const userStr = localStorage.getItem('user');
    
    if (token && userStr) {
      try {
        const user = JSON.parse(userStr);
        set({ user, isLoggedIn: true });
      } catch (error) {
        localStorage.removeItem('authToken');
        localStorage.removeItem('user');
      }
    }
  }
}));