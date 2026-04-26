import PropTypes from 'prop-types';
import '../styles/LoadingSpinner.css';

function LoadingSpinner({ size = 'medium', color = 'primary', className = '' }) {
  const sizeClass = `spinner-${size}`;
  const colorClass = `spinner-${color}`;
  
  return (
    <div className={`loading-spinner ${sizeClass} ${colorClass} ${className}`}>
      <div className="spinner"></div>
    </div>
  );
}

LoadingSpinner.propTypes = {
  size: PropTypes.oneOf(['small', 'medium', 'large']),
  color: PropTypes.oneOf(['primary', 'secondary', 'light']),
  className: PropTypes.string,
};

export default LoadingSpinner;