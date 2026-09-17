import './Card.css';

export function Card({ children, className = '', title, icon: Icon }) {
  return (
    <div className={`card ${className}`}>
      {(title || Icon) && (
        <div className="card-header">
          {Icon && <Icon className="card-icon" />}
          {title && <h3 className="card-title">{title}</h3>}
        </div>
      )}
      <div className="card-content">
        {children}
      </div>
    </div>
  );
}
