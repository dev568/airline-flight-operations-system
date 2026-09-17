import { useState, useEffect } from 'react';
import { Card } from '../components/Card';
import { Button } from '../components/Button';
import { Table } from '../components/Table';
import { Modal } from '../components/Modal';
import { Plus, Search, Edit, Trash2, Users } from 'lucide-react';
import { crewService } from '../services/crewService';
import './CrewMembers.css';
import './Page.css';

export function CrewMembers() {
  const [crewMembers, setCrewMembers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedMember, setSelectedMember] = useState(null);
  const [searchParams, setSearchParams] = useState({
    employeeId: '',
    email: '',
    role: '',
    status: '',
    baseAirport: '',
  });
  const [formData, setFormData] = useState({
    employeeId: '',
    firstName: '',
    lastName: '',
    email: '',
    role: 'CABIN_CREW',
    status: 'ACTIVE',
    baseAirport: '',
    hireDate: '',
  });

  const crewRoles = ['PILOT', 'COPILOT', 'CABIN_CREW', 'PURSER', 'FLIGHT_ENGINEER'];
  const crewStatuses = ['ACTIVE', 'INACTIVE', 'ON_LEAVE', 'SUSPENDED'];

  useEffect(() => {
    loadCrewMembers();
  }, []);

  const loadCrewMembers = async () => {
    try {
      setLoading(true);
      setError(null);
      const response = await crewService.getAllCrewMembers();
      setCrewMembers(response.data);
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
      const response = await crewService.searchCrewMembers(params);
      setCrewMembers(response.data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleCreate = async () => {
    try {
      setError(null);
      await crewService.createCrewMember(formData);
      setIsModalOpen(false);
      resetForm();
      loadCrewMembers();
    } catch (err) {
      setError(err.message);
    }
  };

  const handleUpdate = async () => {
    try {
      setError(null);
      await crewService.updateCrewMember(selectedMember.id, formData);
      setIsModalOpen(false);
      setSelectedMember(null);
      resetForm();
      loadCrewMembers();
    } catch (err) {
      setError(err.message);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Are you sure you want to delete this crew member?')) return;

    try {
      setError(null);
      await crewService.deleteCrewMember(id);
      loadCrewMembers();
    } catch (err) {
      setError(err.message);
    }
  };

  const openCreateModal = () => {
    resetForm();
    setSelectedMember(null);
    setIsModalOpen(true);
  };

  const openEditModal = (member) => {
    setSelectedMember(member);
    setFormData({
      employeeId: member.employeeId,
      firstName: member.firstName,
      lastName: member.lastName,
      email: member.email,
      role: member.role,
      status: member.status,
      baseAirport: member.baseAirport,
      hireDate: member.hireDate || '',
    });
    setIsModalOpen(true);
  };

  const resetForm = () => {
    setFormData({
      employeeId: '',
      firstName: '',
      lastName: '',
      email: '',
      role: 'CABIN_CREW',
      status: 'ACTIVE',
      baseAirport: '',
      hireDate: '',
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
    { key: 'employeeId', label: 'Employee ID' },
    { key: 'firstName', label: 'First Name' },
    { key: 'lastName', label: 'Last Name' },
    { key: 'email', label: 'Email' },
    {
      key: 'role',
      label: 'Role',
      render: (value) => (
        <span className={`role-badge role-${value.toLowerCase()}`}>{value}</span>
      ),
    },
    {
      key: 'status',
      label: 'Status',
      render: (value) => (
        <span className={`status-badge status-${value.toLowerCase()}`}>{value}</span>
      ),
    },
    { key: 'baseAirport', label: 'Base Airport' },
    {
      key: 'hireDate',
      label: 'Hire Date',
      render: (value) => value ? new Date(value).toLocaleDateString() : '-',
    },
  ];

  if (loading) {
    return (
      <div className="page">
        <div className="page-header">
          <h1>Crew Members</h1>
        </div>
        <div className="loading-state">Loading crew members...</div>
      </div>
    );
  }

  return (
    <div className="page">
      <div className="page-header">
        <h1>Crew Members</h1>
        <Button onClick={openCreateModal} icon={Plus}>
          Add Crew Member
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
            <label>Employee ID</label>
            <input
              type="text"
              name="employeeId"
              value={searchParams.employeeId}
              onChange={handleSearchChange}
              placeholder="e.g., EMP001"
            />
          </div>
          <div className="filter-group">
            <label>Email</label>
            <input
              type="email"
              name="email"
              value={searchParams.email}
              onChange={handleSearchChange}
              placeholder="e.g., john@example.com"
            />
          </div>
          <div className="filter-group">
            <label>Role</label>
            <select name="role" value={searchParams.role} onChange={handleSearchChange}>
              <option value="">All Roles</option>
              {crewRoles.map((role) => (
                <option key={role} value={role}>
                  {role}
                </option>
              ))}
            </select>
          </div>
          <div className="filter-group">
            <label>Status</label>
            <select name="status" value={searchParams.status} onChange={handleSearchChange}>
              <option value="">All Statuses</option>
              {crewStatuses.map((status) => (
                <option key={status} value={status}>
                  {status}
                </option>
              ))}
            </select>
          </div>
          <div className="filter-group">
            <label>Base Airport</label>
            <input
              type="text"
              name="baseAirport"
              value={searchParams.baseAirport}
              onChange={handleSearchChange}
              placeholder="e.g., JFK"
              maxLength={3}
              style={{ textTransform: 'uppercase' }}
            />
          </div>
          <Button onClick={handleSearch} icon={Search}>
            Search
          </Button>
          <Button onClick={loadCrewMembers} variant="secondary">
            Reset
          </Button>
        </div>
      </Card>

      <Card>
        <Table
          columns={columns}
          data={crewMembers}
          emptyMessage="No crew members found"
          onRowClick={openEditModal}
        />
      </Card>

      <Modal
        isOpen={isModalOpen}
        onClose={() => {
          setIsModalOpen(false);
          setSelectedMember(null);
          resetForm();
        }}
        title={selectedMember ? 'Edit Crew Member' : 'Add Crew Member'}
        size="large"
      >
        <form onSubmit={(e) => { e.preventDefault(); selectedMember ? handleUpdate() : handleCreate(); }} className="crew-form">
          <div className="form-grid">
            <div className="form-group">
              <label htmlFor="employeeId">Employee ID *</label>
              <input
                type="text"
                id="employeeId"
                name="employeeId"
                value={formData.employeeId}
                onChange={handleInputChange}
                placeholder="e.g., EMP001"
                required
                minLength={1}
                maxLength={50}
              />
            </div>

            <div className="form-group">
              <label htmlFor="email">Email *</label>
              <input
                type="email"
                id="email"
                name="email"
                value={formData.email}
                onChange={handleInputChange}
                placeholder="e.g., john@example.com"
                required
                maxLength={255}
              />
            </div>

            <div className="form-group">
              <label htmlFor="firstName">First Name *</label>
              <input
                type="text"
                id="firstName"
                name="firstName"
                value={formData.firstName}
                onChange={handleInputChange}
                placeholder="e.g., John"
                required
                maxLength={100}
              />
            </div>

            <div className="form-group">
              <label htmlFor="lastName">Last Name *</label>
              <input
                type="text"
                id="lastName"
                name="lastName"
                value={formData.lastName}
                onChange={handleInputChange}
                placeholder="e.g., Smith"
                required
                maxLength={100}
              />
            </div>

            <div className="form-group">
              <label htmlFor="role">Role *</label>
              <select id="role" name="role" value={formData.role} onChange={handleInputChange} required>
                {crewRoles.map((role) => (
                  <option key={role} value={role}>
                    {role}
                  </option>
                ))}
              </select>
            </div>

            <div className="form-group">
              <label htmlFor="status">Status *</label>
              <select id="status" name="status" value={formData.status} onChange={handleInputChange} required>
                {crewStatuses.map((status) => (
                  <option key={status} value={status}>
                    {status}
                  </option>
                ))}
              </select>
            </div>

            <div className="form-group">
              <label htmlFor="baseAirport">Base Airport *</label>
              <input
                type="text"
                id="baseAirport"
                name="baseAirport"
                value={formData.baseAirport}
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
              <label htmlFor="hireDate">Hire Date *</label>
              <input
                type="date"
                id="hireDate"
                name="hireDate"
                value={formData.hireDate}
                onChange={handleInputChange}
                required
              />
            </div>
          </div>

          <div className="form-actions">
            <Button type="button" variant="secondary" onClick={() => setIsModalOpen(false)}>
              Cancel
            </Button>
            <Button type="submit" icon={selectedMember ? Edit : Plus}>
              {selectedMember ? 'Update Crew Member' : 'Add Crew Member'}
            </Button>
          </div>
        </form>
      </Modal>
    </div>
  );
}
