import type { AuraBaseDesignTokens } from "@openng/optimus-ui-themes/aura/base";

type Semantic = AuraBaseDesignTokens['semantic'];

export const semantic: Semantic = {
    primary: {
        50: '{blue.50}',
        100: '{blue.100}',
        200: '{blue.200}',
        300: '{blue.300}',
        400: '{blue.400}',
        500: '{blue.500}',
        600: '{blue.600}',
        700: '{blue.700}',
        800: '{blue.800}',
        900: '{blue.900}',
        950: '{blue.950}',
    },
    colorScheme: {
        light: {
            surface: {
                0: '#FFFFFF',
                50: '{gray.50}',
                100: '{gray.100}',
                200: '{gray.200}',
                300: '{gray.300}',
                400: '{gray.400}',
                500: '{gray.500}',
                600: '{gray.600}',
                700: '{gray.700}',
                800: '{gray.800}',
                900: '{gray.900}',
                950: '{gray.950}',
            },
            primary: {
                color: '{primary.800}',
                contrastColor: '{surface.0}',
                hoverColor: '{primary.900}',
                activeColor: '{primary.950}',
            },
            mask: {
                background: 'rgba(23,31,38,0.4)',
                color: '{surface.200}',
            },
            formField: {
                invalidBorderColor: '{red.400}',
            }
        },
    },
    focusRing: {
        width: '2px',
        offset: '-2px',
        style: 'solid',
        color: '{primary.color}',
    },
};