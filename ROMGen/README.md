# ROM/PLD generator for EGA-2-RGBS board

This tool generates a ROM image or a truth table (that can be then simplified with espresso) that can be used on the EGA-2-RGBS board.

TODO: Write some more documentation

## Example

Table generated for a PLD:

```text
obh = (bh);

obl = (!mode&bl) | (mode&gli);

ogh = (gh&bfix) | (gh&!rh) | (bh&gh) | (gh&gli) | (!mode&gh);

ogl = (mode&!bh&gh&rh&!bfix) | (gli);

orh = (rh);

orl = (!mode&rl) | (mode&gli);

ocs = (vs&!hs) | (!vs&hs);

oncs = (!vs&!hs) | (vs&hs);
```