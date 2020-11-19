/**
 * Created by emamoshin on 01.06.2017.
 */
import React from 'react';
import PropTypes from 'prop-types';
import classNames from 'classnames';
import find from 'lodash/find';
import isEmpty from 'lodash/isEmpty';
import some from 'lodash/some';
import isEqual from 'lodash/isEqual';
import map from 'lodash/map';
import filter from 'lodash/filter';
import first from 'lodash/first';
import get from 'lodash/get';

import TabNav from './TabNav';
import TabNavItem from './TabNavItem';
import TabContent from './TabContent';

/**
 * Компонент контейнера табов
 * @reactProps {string} className - css-класс
 * @reactProps {string} navClassName - css-класс для нава
 * @reactProps {function} onChangeActive
 * @reactProps {node} children - элемент потомок компонента Tabs
 * @reactProps {function} hideSingleTab - скрывать / не скрывать навигацию таба, если он единственный
 * @example
 * <Tabs>
 * {
 *   containers.map((cnt) =>
 *     <Tab id={cnt.id}
 *          title={cnt.name || cnt.id}
 *          active={cnt.opened}>
 *       <WidgetFactory containerId={cnt.id} pageId={cnt.pageId} fetchOnInit={cnt.fetchOnInit} {...cnt.widget} />
 *     </Tab>
 *   )
 * }
 * </Tabs>
 */

class Tabs extends React.Component {
  componentDidUpdate(prevProps, prevState, snapshot) {
    const { onChangeActive, children } = this.props;

    const activeTabVisibility = (children, activeId) => {
      const entity = first(filter(children, child => child.key === activeId));

      return get(entity, 'props.visible');
    };

    const isActiveTabVisibilityHasChange =
      activeTabVisibility(children, prevProps.activeId) !==
      activeTabVisibility(prevProps.children, prevProps.activeId);

    const firstVisibleTab = first(
      filter(children, child => child.props.visible)
    );

    if (isActiveTabVisibilityHasChange) {
      onChangeActive(firstVisibleTab.key, prevProps.activeId);
    }
  }

  /**
   * установка активного таба
   * @param event
   * @param id
   * @param prevId
   */
  handleChangeActive = (event, id, prevId) => {
    this.props.onChangeActive(id, prevId);
  };

  /**
   * getter для айдишника активного таба
   * @return {Array|*}
   */
  get defaultOpenedId() {
    const { children, activeId } = this.props;

    if (activeId) {
      return activeId;
    }

    const foundChild = find(React.Children.toArray(children), child => {
      return child.props.active;
    });
    return foundChild && foundChild.props.id;
  }

  /**
   * Базовый рендер
   * @return {XML}
   */
  render() {
    const {
      className,
      navClassName,
      children,
      hideSingleTab,
      dependencyVisible,
    } = this.props;

    const activeId = this.defaultOpenedId;

    const tabNavItems = React.Children.map(children, child => {
      const { id, title, icon, disabled, visible } = child.props;

      const hasSingleVisibleTab =
        children.filter(child => child.props.visible).length === 1;

      if (
        (hasSingleVisibleTab && hideSingleTab) ||
        !dependencyVisible ||
        !visible
      ) {
        return null;
      }

      return (
        <TabNavItem
          id={id}
          title={title}
          icon={icon}
          disabled={disabled}
          active={activeId === id}
          onClick={this.handleChangeActive}
        />
      );
    });
    const style = { marginBottom: 2 };

    return (
      <div className={className} style={style}>
        {!isEmpty(tabNavItems) && (
          <TabNav className={navClassName}>{tabNavItems}</TabNav>
        )}
        <div
          className={classNames('n2o-tab-content__container', {
            visible: dependencyVisible,
          })}
        >
          <TabContent>
            {React.Children.map(children, child =>
              React.cloneElement(child, {
                active: activeId === child.props.id,
              })
            )}
          </TabContent>
        </div>
      </div>
    );
  }
}

Tabs.propTypes = {
  /**
   * Класс
   */
  className: PropTypes.string,
  /**
   * Класс навигации
   */
  navClassName: PropTypes.string,
  /**
   * Callback на изменение активного таба
   */
  onChangeActive: PropTypes.func,
  children: PropTypes.node,
};

Tabs.defaultProps = {
  onChangeActive: () => {},
};

export default Tabs;
