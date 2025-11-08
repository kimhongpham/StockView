import { create } from "zustand";

interface User {
  id: string;
  username: string;
  role: string;
  avatar?: string;
  email: string;
  token?: string;
}

interface AuthState {
  user: User | null;
  isLoggedIn: boolean;
  login: (user: User) => void;
  logout: () => void;
  setUser: (user: User) => void;
  initializeAuth: () => void;
}

export const useAuthStore = create<AuthState>((set) => ({
  user: null,
  isLoggedIn: false,

  login: (user) => {
    localStorage.setItem("authToken", user.token || "");
    localStorage.setItem("user", JSON.stringify(user));
    set({ user, isLoggedIn: true });
  },

  logout: () => {
    localStorage.removeItem("authToken");
    localStorage.removeItem("user");
    set({ user: null, isLoggedIn: false });
  },

  setUser: (user) => {
    const savedToken = localStorage.getItem("authToken");
    const newUser = { ...user, token: user.token || savedToken || undefined };
    localStorage.setItem("user", JSON.stringify(newUser));
    set({ user: newUser, isLoggedIn: true });
  },

  initializeAuth: () => {
    const token = localStorage.getItem("authToken");
    const userStr = localStorage.getItem("user");

    if (token && userStr) {
      try {
        const user = JSON.parse(userStr);
        set({ user, isLoggedIn: true });
      } catch (error) {
        localStorage.removeItem("authToken");
        localStorage.removeItem("user");
      }
    }
  },
}));
