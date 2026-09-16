import { definePreset } from "@openng/optimus-ui-themes";
import Aura from '@openng/optimus-ui-themes/aura';
import { primitive } from './primitive';
import { semantic } from './semantic';
import { primitiveExtends } from "./extends/primitive-extends";
import { semanticExtends } from "./extends/semantic-extends";

export const eakteTheme = definePreset(Aura, {
    primitive,
    semantic,
    components: {
        // Selber angepasste Komponenten hier
    },
    extend: { ...primitiveExtends, ...semanticExtends}
})