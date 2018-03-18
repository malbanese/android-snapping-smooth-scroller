An advanced implementation of the default RecyclerView smooth scroller that aids in
positioning and scrolling to focused views.

The snapping mechanism relies on RecyclerView's smooth scrolling capability. When a view gains focus, it will trigger a smooth scroll action within it's parent RecyclerView. 

### Example configured for center snapping
```java
  // Create an options object with a snap animation duration of 350ms, and snap points configured to the center of the screen
  SnappingSmoothScroller.SnappingOptions options = new SnappingSmoothScroller.SnappingOptions();
  options.setChildSnappingLocation(0.5f);
  options.setParentSnappingLocation(0.5f);
  options.setSnapDuration(350);
  
  SnappingLinearLayoutManager layoutManager = new SnappingLinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
  layoutManager.setSnappingOptions(mSnappingOptions);
```

## Example of the different playground options available in the test app.
<img src="https://i.imgur.com/sglOqAR.png" alt="Snapping Sandbox Options" width="500">

## Example of a snapping configuration
<img src="https://i.imgur.com/DRDsMGW.gif" alt="Snapping Example" width="500">

## Example of a center snapping configuration
<img src="https://i.imgur.com/6WPFr4G.gif" alt="Center Snapping Example" width="500">
