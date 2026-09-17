import './Button.css';

export function Button({ children, variant = 'primary', size = 'medium', onClick, disabled, type = 'button', icon: Icon }) {
  return (
    <button
      type={type}
      className={`button button-${variant} button-${size}`}
      onClick={onClick}
      disabled={disabled}
    >
      {Icon && <Icon className="button-icon" />}
      {children}
    </button>
  );
}
