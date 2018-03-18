An advanced implementation of the default RecyclerView smooth scroller that aids in
positioning and scrolling to focused views. The snapping mechanism relies on RecyclerView's smooth scrolling capability. When a view gains focus, it will trigger a smooth scroll action within it's parent RecyclerView. 

### Snapping Options
- Snapping with percentage or pixel value
- Configure different snapping points for parent and children
- Snap animation duration
- Snap animation interpolator
- View seeking speed (for when a view is far offscreen)
- Ability to include margins in the snapping calculation
- Ability to include decorations in the snapping calculation

### Example configured for center snapping
```java
  // Create an options object with a snap animation duration of 350ms, and snap points configured to the center of the screen
  SnappingSmoothScroller.SnappingOptions options = new SnappingSmoothScroller.SnappingOptions();
  
  // In this case, 0.5f sets the snapping location to a percentage of the parent / children. Since both are set to 50%, the center
  // of the child, will be snapped to the center of the parent. If a value greater than 1.0 is used here, it will be calculated
  // as a pixel value. For example, setting the parent snap location to 150 would make the center of the child snap to 150px into
  // the parent.
  options.setChildSnappingLocation(0.5f);
  options.setParentSnappingLocation(0.5f);
  options.setSnapDuration(350);
  
  SnappingLinearLayoutManager layoutManager = new SnappingLinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
  layoutManager.setSnappingOptions(mSnappingOptions);
```
##### SnappingSmoothScroller
Custom smooth scroller implementation, used internally by the layout manager extensions in order to provide the snapping behavior.

##### SnappingSmoothScroller.SnappingOptions
Options object for the snapping smoooth scroller, should be provided to the layout manager extensions in order to configure the desired snapping behavior.

##### SnappingLinearLayoutManager
Extension of LinearLayoutManager which should be used in its place. The snapping options object can be passed in via the setSnappingOptions() method. 

### Playground example application
This project contains a playground application so that the different snapping configurations can be modified and viewed in real time. The opaque green / blue lines represent the parent's start / end snapping point. The transparent green / blue lines represent the child's start / end snapping point.

An options menu can be opened via the 'o' keypress.

#### Example of the different playground options available in the test app.
<img src="https://i.imgur.com/sglOqAR.png" alt="Snapping Sandbox Options" width="500">

#### Example of a snapping configuration
<img src="https://i.imgur.com/DRDsMGW.gif" alt="Snapping Example" width="500">

#### Example of a center snapping configuration
<img src="https://i.imgur.com/6WPFr4G.gif" alt="Center Snapping Example" width="500">
