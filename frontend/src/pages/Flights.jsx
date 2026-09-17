import { useState, useEffect } from 'react';
import { Card } from '../components/Card';
import { Button } from '../components/Button';
import { Table } from '../components/Table';
import { Modal } from '../components/Modal';
import { Plus, Search, Edit, Trash2, Plane } from 'lucide-react';
import { flightService } from '../services/flightService';
import './Flights.css';
import './Page.css';

export function Flights() {
  const [flights, setFlights] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedFlight, setSelectedFlight] = useState(null);
  const [searchParams, setSearchParams] = useState({
    flightNumber: '',
    status: '',
    departureAirport: '',
    arrivalAirport: '',
  });
  const [formData, setFormData] = useState({
    flightNumber: '',
    departureAirport: '',
    arrivalAirport: '',
    scheduledDeparture: '',
    scheduledArrival: '',
    status: 'SCHEDULED',
    aircraftType: '',
  });

  const flightStatuses = ['SCHEDULED', 'BOARDING', 'DEPARTED', 'ARRIVED', 'DELAYED', 'CANCELLED'];

  useEffect(() => {
    loadFlights();
  }, []);

  const loadFlights = async () => {
    try {
      setLoading(true);
      setError(null);
      const response = await flightService.getAllFlights();
      setFlights(response.data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = async () => {
    try {
      setLoading(true);
      setError(null);
      const params = Object.fromEntries(
        Object.entries(searchParams).filter(([_, value]) => value !== '')
      );
      const response = await flightService.searchFlights(params);
      setFlights(response.data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleCreate = async () => {
    try {
      setError(null);
      await flightService.createFlight(formData);
      setIsModalOpen(false);
      resetForm();
      loadFlights();
    } catch (err) {
      setError(err.message);
    }
  };

  const handleUpdate = async () => {
    try {
      setError(null);
      await flightService.updateFlight(selectedFlight.id, formData);
      setIsModalOpen(false);
      setSelectedFlight(null);
      resetForm();
      loadFlights();
    } catch (err) {
      setError(err.message);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Are you sure you want to delete this flight?')) return;

    try {
      setError(null);
      await flightService.deleteFlight(id);
      loadFlights();
    } catch (err) {
      setError(err.message);
    }
  };

  const openCreateModal = () => {
    resetForm();
    setSelectedFlight(null);
    setIsModalOpen(true);
  };

  const openEditModal = (flight) => {
    setSelectedFlight(flight);
    setFormData({
      flightNumber: flight.flightNumber,
      departureAirport: flight.departureAirport,
      arrivalAirport: flight.arrivalAirport,
      scheduledDeparture: flight.scheduledDeparture?.substring(0, 16) || '',
      scheduledArrival: flight.scheduledArrival?.substring(0, 16) || '',
      status: flight.status,
      aircraftType: flight.aircraftType || '',
    });
    setIsModalOpen(true);
  };

  const resetForm = () => {
    setFormData({
      flightNumber: '',
      departureAirport: '',
      arrivalAirport: '',
      scheduledDeparture: '',
      scheduledArrival: '',
      status: 'SCHEDULED',
      aircraftType: '',
    });
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSearchChange = (e) => {
    const { name, value } = e.target;
    setSearchParams((prev) => ({ ...prev, [name]: value }));
  };

  const columns = [
    { key: 'flightNumber', label: 'Flight Number' },
    { key: 'departureAirport', label: 'Departure' },
    { key: 'arrivalAirport', label: 'Arrival' },
    {
      key: 'scheduledDeparture',
      label: 'Scheduled Departure',
      render: (value) => new Date(value).toLocaleString(),
    },
    {
      key: 'scheduledArrival',
      label: 'Scheduled Arrival',
      render: (value) => new Date(value).toLocaleString(),
    },
    {
      key: 'status',
      label: 'Status',
      render: (value) => (
        <span className={`status-badge status-${value.toLowerCase()}`}>{value}</span>
      ),
    },
    {
      key: 'aircraftType',
      label: 'Aircraft',
      render: (value) => value || '-',
    },
  ];

  if (loading) {
    return (
      <div className="page">
        <div className="page-header">
          <h1>Flights</h1>
        </div>
        <div className="loading-state">Loading flights...</div>
      </div>
    );
  }

  return (
    <div className="page">
      <div className="page-header">
        <h1>Flights</h1>
        <Button onClick={openCreateModal} icon={Plus}>
          Create Flight
        </Button>
      </div>

      {error && (
        <div className="error-banner">
          <p>{error}</p>
          <Button onClick={() => setError(null)} variant="secondary" size="small">
            Dismiss
          </Button>
        </div>
      )}

      <Card className="search-card">
        <div className="search-filters">
          <div className="filter-group">
            <label>Flight Number</label>
            <input
              type="text"
              name="flightNumber"
              value={searchParams.flightNumber}
              onChange={handleSearchChange}
              placeholder="e.g., UA123"
            />
          </div>
          <div className="filter-group">
            <label>Status</label>
            <select name="status" value={searchParams.status} onChange={handleSearchChange}>
              <option value="">All Statuses</option>
              {flightStatuses.map((status) => (
                <option key={status} value={status}>
                  {status}
                </option>
              ))}
            </select>
          </div>
          <div className="filter-group">
            <label>Departure Airport</label>
            <input
              type="text"
              name="departureAirport"
              value={searchParams.departureAirport}
              onChange={handleSearchChange}
              placeholder="e.g., JFK"
            />
          </div>
          <div className="filter-group">
            <label>Arrival Airport</label>
            <input
              type="text"
              name="arrivalAirport"
              value={searchParams.arrivalAirport}
              onChange={handleSearchChange}
              placeholder="e.g., LAX"
            />
          </div>
          <Button onClick={handleSearch} icon={Search}>
            Search
          </Button>
          <Button onClick={loadFlights} variant="secondary">
            Reset
          </Button>
        </div>
      </Card>

      <Card>
        <Table
          columns={columns}
          data={flights}
          emptyMessage="No flights found"
          onRowClick={openEditModal}
        />
      </Card>

      <Modal
        isOpen={isModalOpen}
        onClose={() => {
          setIsModalOpen(false);
          setSelectedFlight(null);
          resetForm();
        }}
        title={selectedFlight ? 'Edit Flight' : 'Create Flight'}
        size="large"
      >
        <form onSubmit={(e) => { e.preventDefault(); selectedFlight ? handleUpdate() : handleCreate(); }} className="flight-form">
          <div className="form-grid">
            <div className="form-group">
              <label htmlFor="flightNumber">Flight Number *</label>
              <input
                type="text"
                id="flightNumber"
                name="flightNumber"
                value={formData.flightNumber}
                onChange={handleInputChange}
                placeholder="e.g., UA123"
                required
                pattern="^[A-Z]{2}[0-9]{3,4}$"
                title="Format: XX123 or XX1234"
              />
              <small>Format: XX123 or XX1234 (e.g., UA123, BA1234)</small>
            </div>

            <div className="form-group">
              <label htmlFor="departureAirport">Departure Airport *</label>
              <input
                type="text"
                id="departureAirport"
                name="departureAirport"
                value={formData.departureAirport}
                onChange={handleInputChange}
                placeholder="e.g., JFK"
                required
                pattern="^[A-Z]{3}$"
                title="3-letter IATA code"
                maxLength={3}
                style={{ textTransform: 'uppercase' }}
              />
              <small>3-letter IATA code (e.g., JFK, LAX)</small>
            </div>

            <div className="form-group">
              <label htmlFor="arrivalAirport">Arrival Airport *</label>
              <input
                type="text"
                id="arrivalAirport"
                name="arrivalAirport"
                value={formData.arrivalAirport}
                onChange={handleInputChange}
                placeholder="e.g., LAX"
                required
                pattern="^[A-Z]{3}$"
                title="3-letter IATA code"
                maxLength={3}
                style={{ textTransform: 'uppercase' }}
              />
              <small>3-letter IATA code (e.g., JFK, LAX)</small>
            </div>

            <div className="form-group">
              <label htmlFor="status">Status *</label>
              <select id="status" name="status" value={formData.status} onChange={handleInputChange} required>
                {flightStatuses.map((status) => (
                  <option key={status} value={status}>
                    {status}
                  </option>
                ))}
              </select>
            </div>

            <div className="form-group">
              <label htmlFor="scheduledDeparture">Scheduled Departure *</label>
              <input
                type="datetime-local"
                id="scheduledDeparture"
                name="scheduledDeparture"
                value={formData.scheduledDeparture}
                onChange={handleInputChange}
                required
              />
            </div>

            <div className="form-group">
              <label htmlFor="scheduledArrival">Scheduled Arrival *</label>
              <input
                type="datetime-local"
                id="scheduledArrival"
                name="scheduledArrival"
                value={formData.scheduledArrival}
                onChange={handleInputChange}
                required
              />
            </div>

            <div className="form-group full-width">
              <label htmlFor="aircraftType">Aircraft Type</label>
              <input
                type="text"
                id="aircraftType"
                name="aircraftType"
                value={formData.aircraftType}
                onChange={handleInputChange}
                placeholder="e.g., Boeing 737-800"
                maxLength={50}
              />
            </div>
          </div>

          <div className="form-actions">
            <Button type="button" variant="secondary" onClick={() => setIsModalOpen(false)}>
              Cancel
            </Button>
            <Button type="submit" icon={selectedFlight ? Edit : Plus}>
              {selectedFlight ? 'Update Flight' : 'Create Flight'}
            </Button>
          </div>
        </form>
      </Modal>
    </div>
  );
}
