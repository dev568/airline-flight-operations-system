import { useState, useEffect } from 'react';
import { Card } from '../components/Card';
import { Button } from '../components/Button';
import { Table } from '../components/Table';
import { Modal } from '../components/Modal';
import { Plus, Search, Edit, Trash2, ClipboardList } from 'lucide-react';
import { operationService } from '../services/operationService';
import { flightService } from '../services/flightService';
import './FlightOperations.css';
import './Page.css';

export function FlightOperations() {
  const [operations, setOperations] = useState([]);
  const [flights, setFlights] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedOperation, setSelectedOperation] = useState(null);
  const [searchParams, setSearchParams] = useState({
    flightId: '',
    operationReference: '',
    status: '',
    airportCode: '',
  });
  const [formData, setFormData] = useState({
    flightId: '',
    operationReference: '',
    status: 'PLANNED',
    scheduledAt: '',
    actualAt: '',
    airportCode: '',
    remarks: '',
  });

  const operationStatuses = ['PLANNED', 'CHECK_IN_OPEN', 'BOARDING', 'DEPARTED', 'ARRIVED', 'DELAYED', 'CANCELLED', 'COMPLETED'];

  useEffect(() => {
    loadOperations();
    loadFlights();
  }, []);

  const loadOperations = async () => {
    try {
      setLoading(true);
      setError(null);
      const response = await operationService.getAllOperations();
      setOperations(response.data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const loadFlights = async () => {
    try {
      const response = await flightService.getAllFlights();
      setFlights(response.data);
    } catch (err) {
      console.error('Failed to load flights:', err);
    }
  };

  const handleSearch = async () => {
    try {
      setLoading(true);
      setError(null);
      const params = Object.fromEntries(
        Object.entries(searchParams).filter(([_, value]) => value !== '')
      );
      const response = await operationService.searchOperations(params);
      setOperations(response.data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleCreate = async () => {
    try {
      setError(null);
      const requestData = {
        flightId: formData.flightId || undefined,
        operationReference: formData.operationReference,
        status: formData.status,
        scheduledAt: formData.scheduledAt ? new Date(formData.scheduledAt).toISOString() : undefined,
        actualAt: formData.actualAt ? new Date(formData.actualAt).toISOString() : undefined,
        airportCode: formData.airportCode,
        remarks: formData.remarks || undefined,
      };
      await operationService.createOperation(requestData);
      setIsModalOpen(false);
      resetForm();
      loadOperations();
    } catch (err) {
      setError(err.message);
    }
  };

  const handleUpdate = async () => {
    try {
      setError(null);
      const requestData = {
        status: formData.status,
        scheduledAt: formData.scheduledAt ? new Date(formData.scheduledAt).toISOString() : undefined,
        actualAt: formData.actualAt ? new Date(formData.actualAt).toISOString() : undefined,
        airportCode: formData.airportCode,
        remarks: formData.remarks || undefined,
      };
      await operationService.updateOperation(selectedOperation.id, requestData);
      setIsModalOpen(false);
      setSelectedOperation(null);
      resetForm();
      loadOperations();
    } catch (err) {
      setError(err.message);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Are you sure you want to delete this operation?')) return;

    try {
      setError(null);
      await operationService.deleteOperation(id);
      loadOperations();
    } catch (err) {
      setError(err.message);
    }
  };

  const openCreateModal = () => {
    resetForm();
    setSelectedOperation(null);
    setIsModalOpen(true);
    // Reload flights to ensure we have the latest data for the dropdown
    loadFlights();
  };

  const openEditModal = (operation) => {
    setSelectedOperation(operation);
    setFormData({
      flightId: operation.flightId || '',
      operationReference: operation.operationReference,
      status: operation.status,
      scheduledAt: operation.scheduledAt ? new Date(operation.scheduledAt).toISOString().substring(0, 16) : '',
      actualAt: operation.actualAt ? new Date(operation.actualAt).toISOString().substring(0, 16) : '',
      airportCode: operation.airportCode,
      remarks: operation.remarks || '',
    });
    setIsModalOpen(true);
  };

  const resetForm = () => {
    setFormData({
      flightId: '',
      operationReference: '',
      status: 'PLANNED',
      scheduledAt: '',
      actualAt: '',
      airportCode: '',
      remarks: '',
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

  const getFlightNumber = (flightId) => {
    const flight = flights.find(f => f.id === flightId);
    return flight ? flight.flightNumber : 'Unknown';
  };

  const columns = [
    {
      key: 'operationReference',
      label: 'Reference',
      render: (value) => <span className="reference-badge">{value}</span>,
    },
    {
      key: 'flightId',
      label: 'Flight',
      render: (value) => getFlightNumber(value),
    },
    {
      key: 'status',
      label: 'Status',
      render: (value) => (
        <span className={`status-badge status-${value.toLowerCase()}`}>{value}</span>
      ),
    },
    {
      key: 'scheduledAt',
      label: 'Scheduled',
      render: (value) => value ? new Date(value).toLocaleString() : '-',
    },
    {
      key: 'actualAt',
      label: 'Actual',
      render: (value) => value ? new Date(value).toLocaleString() : '-',
    },
    { key: 'airportCode', label: 'Airport' },
    {
      key: 'remarks',
      label: 'Remarks',
      render: (value) => value ? value.substring(0, 50) + (value.length > 50 ? '...' : '') : '-',
    },
  ];

  if (loading) {
    return (
      <div className="page">
        <div className="page-header">
          <h1>Flight Operations</h1>
        </div>
        <div className="loading-state">Loading operations...</div>
      </div>
    );
  }

  return (
    <div className="page">
      <div className="page-header">
        <h1>Flight Operations</h1>
        <Button onClick={openCreateModal} icon={Plus}>
          Create Operation
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
            <label>Operation Reference</label>
            <input
              type="text"
              name="operationReference"
              value={searchParams.operationReference}
              onChange={handleSearchChange}
              placeholder="e.g., OP-001"
            />
          </div>
          <div className="filter-group">
            <label>Status</label>
            <select name="status" value={searchParams.status} onChange={handleSearchChange}>
              <option value="">All Statuses</option>
              {operationStatuses.map((status) => (
                <option key={status} value={status}>
                  {status}
                </option>
              ))}
            </select>
          </div>
          <div className="filter-group">
            <label>Airport Code</label>
            <input
              type="text"
              name="airportCode"
              value={searchParams.airportCode}
              onChange={handleSearchChange}
              placeholder="e.g., JFK"
              maxLength={3}
              style={{ textTransform: 'uppercase' }}
            />
          </div>
          <Button onClick={handleSearch} icon={Search}>
            Search
          </Button>
          <Button onClick={loadOperations} variant="secondary">
            Reset
          </Button>
        </div>
      </Card>

      <Card>
        <Table
          columns={columns}
          data={operations}
          emptyMessage="No operations found"
          onRowClick={openEditModal}
        />
      </Card>

      <Modal
        isOpen={isModalOpen}
        onClose={() => {
          setIsModalOpen(false);
          setSelectedOperation(null);
          resetForm();
        }}
        title={selectedOperation ? 'Edit Operation' : 'Create Operation'}
        size="large"
      >
        <form onSubmit={(e) => { e.preventDefault(); selectedOperation ? handleUpdate() : handleCreate(); }} className="operation-form">
          <div className="form-grid">
            {selectedOperation ? (
              <div className="form-group">
                <label>Flight</label>
                <input
                  type="text"
                  value={getFlightNumber(selectedOperation.flightId)}
                  disabled
                  style={{ backgroundColor: '#f1f5f9' }}
                />
              </div>
            ) : (
              <div className="form-group">
                <label htmlFor="flightId">Flight *</label>
                <select id="flightId" name="flightId" value={formData.flightId} onChange={handleInputChange} required>
                  <option value="">Select Flight</option>
                  {flights.length === 0 ? (
                    <option value="" disabled>No flights available</option>
                  ) : (
                    flights.map((flight) => (
                      <option key={flight.id} value={flight.id}>
                        {flight.flightNumber} - {flight.departureAirport} to {flight.arrivalAirport}
                      </option>
                    ))
                  )}
                </select>
                {flights.length === 0 && <small style={{ color: '#ef4444' }}>Please create a flight first</small>}
              </div>
            )}

            <div className="form-group">
              <label htmlFor="operationReference">Operation Reference *</label>
              <input
                type="text"
                id="operationReference"
                name="operationReference"
                value={formData.operationReference}
                onChange={handleInputChange}
                placeholder="e.g., OP-001"
                required
                minLength={1}
                maxLength={50}
              />
            </div>

            <div className="form-group">
              <label htmlFor="status">Status *</label>
              <select id="status" name="status" value={formData.status} onChange={handleInputChange} required>
                {operationStatuses.map((status) => (
                  <option key={status} value={status}>
                    {status}
                  </option>
                ))}
              </select>
            </div>

            <div className="form-group">
              <label htmlFor="airportCode">Airport Code *</label>
              <input
                type="text"
                id="airportCode"
                name="airportCode"
                value={formData.airportCode}
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
              <label htmlFor="scheduledAt">Scheduled Time *</label>
              <input
                type="datetime-local"
                id="scheduledAt"
                name="scheduledAt"
                value={formData.scheduledAt}
                onChange={handleInputChange}
                required
              />
            </div>

            <div className="form-group">
              <label htmlFor="actualAt">Actual Time</label>
              <input
                type="datetime-local"
                id="actualAt"
                name="actualAt"
                value={formData.actualAt}
                onChange={handleInputChange}
              />
            </div>

            <div className="form-group full-width">
              <label htmlFor="remarks">Remarks</label>
              <textarea
                id="remarks"
                name="remarks"
                value={formData.remarks}
                onChange={handleInputChange}
                placeholder="Additional notes..."
                rows={3}
                maxLength={500}
              />
              <small>{formData.remarks.length}/500 characters</small>
            </div>
          </div>

          <div className="form-actions">
            <Button type="button" variant="secondary" onClick={() => setIsModalOpen(false)}>
              Cancel
            </Button>
            <Button type="submit" icon={selectedOperation ? Edit : Plus}>
              {selectedOperation ? 'Update Operation' : 'Create Operation'}
            </Button>
          </div>
        </form>
      </Modal>
    </div>
  );
}
