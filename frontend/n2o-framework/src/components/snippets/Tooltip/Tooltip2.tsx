import React from 'react'
import classNames from 'classnames'
import { PropsGetterArgs } from 'react-popper-tooltip/dist/types'
import 'react-popper-tooltip/dist/styles.css'

interface ITooltipProps {
    hint?: string | number | React.Component
    className?: string
    getArrowProps(args?: PropsGetterArgs): {
        style: React.CSSProperties
        'data-popper-arrow': boolean
    };
    getTooltipProps(args?: PropsGetterArgs): {
        'data-popper-interactive': boolean | undefined
        style: React.CSSProperties
    };
    setTooltipRef: React.Dispatch<React.SetStateAction<HTMLElement | null>>
    theme: string
}

// тема tooltip body
export const getThemeClass = theme => classNames({
    dark: theme === 'dark',
    light: theme === 'light',
})

export function Tooltip({
    hint,
    className,
    setTooltipRef,
    getTooltipProps,
    getArrowProps,
    theme,
}: ITooltipProps): JSX.Element {
    const themeClassName = getThemeClass(theme)

    return (
        <div
            ref={setTooltipRef}
            {...getTooltipProps(
                { className: classNames('tooltip-container', className, themeClassName) },
            )}
        >
            {hint}
            <div {...getArrowProps({ className: classNames('tooltip-arrow', themeClassName) })} />
        </div>
    )
}
