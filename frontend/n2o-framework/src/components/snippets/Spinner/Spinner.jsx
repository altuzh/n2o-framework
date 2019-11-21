import React, { Component, Fragment } from 'react';
import { Spinner } from 'reactstrap';
import cx from 'classnames';
import { eq, values } from 'lodash';
import PropTypes from 'prop-types';

const TYPE = {
  INLINE: 'inline',
  COVER: 'cover',
};

let Comp = Spinner;

class BaseSpinner extends Component {
  constructor(props) {
    super(props);

    this.state = {
      loading: false,
    };

    this._timeoutId = null;

    this.renderCoverSpiner = this.renderCoverSpiner.bind(this);
    this.renderLineSpinner = this.renderLineSpinner.bind(this);
  }

  static setSpinner(component) {
    Comp = component;
  }

  componentDidMount() {
    const { delay } = this.props;

    this._timeoutId = setTimeout(() => this.setState({ loading: true }), delay);
  }

  componentDidUpdate(prevProps) {
    const { loading, minSpinnerTimeToShow } = this.props;
    const { loading: stateLoading } = this.state;

    if (prevProps.loading && !loading && !stateLoading) {
      clearTimeout(this._timeoutId);

      this.setState({ loading: false });
    } else if (prevProps.loading && !loading && stateLoading) {
      setTimeout(() => this.setState({ loading: false }), minSpinnerTimeToShow);
    }
  }

  renderCoverSpiner() {
    const {
      children,
      className,
      text,
      transparent,
      color,
      ...rest
    } = this.props;
    const { loading } = this.state;

    return (
      <div
        className={cx('n2o-spinner-wrapper', {
          [className]: className,
        })}
      >
        {loading && (
          <Fragment>
            <div className="n2o-spinner-container ">
              <Comp className="spinner-border" color={color} {...rest} />
              <div className="loading_text">{text}</div>
            </div>
            {!transparent ? <div className="spinner-background" /> : null}
          </Fragment>
        )}
        {children}
      </div>
    );
  }

  renderLineSpinner() {
    const { type, children, delay, ...rest } = this.props;
    const { loading } = this.state;

    return loading ? (
      <Comp className="spinner" {...rest} />
    ) : React.Children.count(children) ? (
      children
    ) : null;
  }

  render() {
    const { type } = this.props;

    return eq(type, TYPE.COVER)
      ? this.renderCoverSpiner()
      : this.renderLineSpinner();
  }
}

BaseSpinner.propTypes = {
  loading: PropTypes.bool,
  type: PropTypes.oneOf(values(TYPE)),
  delay: PropTypes.number,
  text: PropTypes.string,
  transparent: PropTypes.bool,
  color: PropTypes.string,
  minSpinnerTimeToShow: PropTypes.number,
};

BaseSpinner.defaultProps = {
  loading: true,
  type: 'inline',
  delay: 400,
  text: '',
  transparent: false,
  color: 'primary',
  minSpinnerTimeToShow: 250,
};

export default BaseSpinner;
