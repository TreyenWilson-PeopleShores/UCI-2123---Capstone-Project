import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import React from "react";
import ReactDom from "react-dom/client";
import './index.css';
import App from './App.jsx';
import Cal from "./components/Cal.jsx"

createRoot(document.getElementById('root')).render(
  <div>
    <App/>
    <Cal/>
    <Cal/>
    <App/>
  </div>,
)
