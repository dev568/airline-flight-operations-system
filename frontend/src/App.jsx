import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { Sidebar } from './components/Sidebar';
import { Header } from './components/Header';
import { Dashboard } from './pages/Dashboard';
import { Flights } from './pages/Flights';
import { CrewMembers } from './pages/CrewMembers';
import { FlightOperations } from './pages/FlightOperations';
import './App.css';

function App() {
  return (
    <Router>
      <div className="app">
        <Sidebar />
        <div className="main-content">
          <Header />
          <Routes>
            <Route path="/" element={<Dashboard />} />
            <Route path="/flights" element={<Flights />} />
            <Route path="/crew" element={<CrewMembers />} />
            <Route path="/operations" element={<FlightOperations />} />
          </Routes>
        </div>
      </div>
    </Router>
  );
}

export default App;
