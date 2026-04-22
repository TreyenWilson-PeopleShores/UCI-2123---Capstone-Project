import PropTypes from 'prop-types';
import '../styles/Skeleton.css';

function EventCardSkeleton({ count = 1 }) {
  const skeletons = Array.from({ length: count }, (_, index) => (
    <div key={index} className="skeleton-card event-card-skeleton">
      <div className="skeleton skeleton-image"></div>
      <div className="skeleton-content">
        <div className="skeleton skeleton-title"></div>
        <div className="skeleton skeleton-text"></div>
        <div className="skeleton skeleton-text short"></div>
        <div className="skeleton skeleton-button"></div>
      </div>
    </div>
  ));

  return <>{skeletons}</>;
}

EventCardSkeleton.propTypes = {
  count: PropTypes.number,
};

export default EventCardSkeleton;