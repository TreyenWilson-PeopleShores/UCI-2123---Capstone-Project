import '../styles/ArrowButton.css';
import PropTypes from 'prop-types';

/**
 * ArrowButton - A reusable arrow button component with consistent styling
 * 
 * @param {Object} props
 * @param {string} props.direction - Direction of arrow: 'left' or 'right'
 * @param {function} props.onClick - Click handler function
 * @param {boolean} props.disabled - Whether the button is disabled
 * @param {string} props.label - Accessible label for screen readers
 * @param {string} props.className - Additional CSS classes
 * @param {Object} props.style - Additional inline styles
 */
function ArrowButton({ 
  direction = 'left', 
  onClick, 
  disabled = false, 
  label = '', 
  className = '', 
  style = {} 
}) {
  const arrowSymbol = direction === 'left' ? '&larr;' : '&rarr;';
  
  return (
    <button
      type="button"
      className={`arrow-button ${className}`}
      onClick={onClick}
      disabled={disabled}
      aria-label={label || `${direction} arrow button`}
      style={style}
      dangerouslySetInnerHTML={{ __html: arrowSymbol }}
    />
  );
}

ArrowButton.propTypes = {
  direction: PropTypes.oneOf(['left', 'right']),
  onClick: PropTypes.func,
  disabled: PropTypes.bool,
  label: PropTypes.string,
  className: PropTypes.string,
  style: PropTypes.object
};

ArrowButton.defaultProps = {
  direction: 'left',
  disabled: false,
  label: '',
  className: '',
  style: {}
};

export default ArrowButton;