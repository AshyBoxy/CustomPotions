# Custom Potions
Provides a server side only way to add custom potions using datapacks.

When installed on the client, it provides integration with REI and EMI.

## Adding a potion
In a datapack, make a data/id/custom_potions/id.json file for your potion.

The available parameters are "fallback" for the fallback name of the potion, if translations are unvailable
(Potion of {fallback}, Arrow of {fallback}, Splash {fallback}, Lingering {fallback}),
an array of effects following the vanilla format, and an optional color using the [same format as vanilla](https://minecraft.wiki/w/Calculators/Decimal_representation_of_color).

For example:
```json
{
    "fallback": "Bad Luck",
    "effects": [
        {
            "id": "minecraft:unluck",
            "duration": 6000,
            "amplifier": 1
        }
    ],
    "color": 5841183
}
```

## Adding a potion recipe
In your datapack, make a data/id/potion_recipes/id.json file for your recipe. The available parameters are "reagent",
which takes in an item id, and is the ingredient used in the top of the brewing stand to make the potion. There are 
also "base" and "result", which both use the same format, and define the input and output potions used in the recipe.
They have the parameters "type", which takes in either "custompotions:vanilla" for vanilla (or modded) potions, and 
"custompotions:custom" for potions added by datapack using this mod.

For example:
```json
{
    "reagent": "minecraft:poisonous_potato",
    "base": {
        "type": "custompotions:vanilla",
        "potion": "minecraft:awkward"
    },
    "result": {
        "type": "custompotions:custom",
        "potion": "potionstuffs:bad_luck"
    }
}
```
```json
{
    "reagent": "minecraft:nautilus_shell",
    "base": {
        "type": "custompotions:vanilla",
        "potion": "minecraft:awkward"
    },
    "result": {
        "type": "custompotions:vanilla",
        "potion": "minecraft:luck"
    }
}
```
## Translating
The translation key follows the format of `custompotion.namespace.id.type`. For example, `custompotion.mypotions.bad_luck.normal`, `custompotion.mypotions.bad_luck.splash`, `custompotion.mypotions.bad_luck.lingering`, 
and `custompotion.mypotions.bad_luck.arrow`.

You can use these in a language json file like so:
```json
{
    "custompotion.mypotions.bad_luck.normal": "Unlucky Potion",
    "custompotion.mypotions.bad_luck.splash": "Throwable Unlucky Potion",
    "custompotion.mypotions.bad_luck.lingering": "Lingering Unlucky Potion",
    "custompotion.mypotions.bad_luck.arrow": "Unlucky Arrow"
}
```
