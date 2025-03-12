import { configureStore } from "@reduxjs/toolkit";
import authReducer from "../slices/authSlice";
import checkInReducer from "../slices/checkInSlice";

const store = configureStore({
  reducer: {
    auth: authReducer,
    checkIn: checkInReducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware({
      serializableCheck: false,
    }),
});

export default store;