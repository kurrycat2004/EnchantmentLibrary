# Enchantment Library

### **1.12.2 only**

-----

### Description

Adds a single block: the Enchantment Library, which can store any number of enchantments. <br>

When inserting an enchanted book, it gets converted into its "point worth". <br>
The "points worth" of an enchantment is <code>2<sup>level</sup></code>, where `level` is the level of the inserted enchantment. <br>
You can then extract any stored enchantment at any level, as long as you have enough points, and you have inserted an enchantment of at least that level before. <br>
So inserting two `Unbreaking II` books and extracting one `Unbreaking III` will NOT work if you have never inserted an `Unbreaking III` book or higher before. <br>

Because of the way the enchantments are stored, there is no upper limit on how many points you can have stored. <br>
The maximum *level* supported for storage/insertion/extraction however is MAX_SHORT (which is also the limit of Vanilla).

The Enchantment Library also exposes the max level extractable of each enchantment as enchanted books to automation (Hoppers, AE2, etc.). <br>
Insertion supports ANY enchanted book (that has enchantments). If a book has multiple enchantments, they will all get stored.

Extracting books in the GUI is possible for any level from 1 to the max extractable. <br>
Clicking with an enchanted book in hand on a different enchantment will add that to the book held (can be disabled in config).
