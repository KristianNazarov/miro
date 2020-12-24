package ru.knazarov.miro.controller;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.knazarov.miro.model.Widget;
import ru.knazarov.miro.service.WidgetOperationsService;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@RestController
public class APIController {

    private WidgetOperationsService widgetOperationsService;
    private WidgetModelAssebler assembler;


    public APIController(WidgetOperationsService widgetOperationsService, WidgetModelAssebler assembler)
    {
        this.widgetOperationsService = widgetOperationsService;
        this.assembler = assembler;
    }

    @GetMapping("/")
    public String gotoSwagger()
    {

        //TODO redirect to swagger
        return "hello, Miro!";
    }

    @GetMapping("/widgets")
    public CollectionModel<EntityModel<Widget>> all()
    {
        List<EntityModel<Widget>> widgets = widgetOperationsService.getWidgets().stream()
            .map(assembler::toModel).collect(Collectors.toList());

        return CollectionModel.of(widgets, linkTo(methodOn(APIController.class).all()).withSelfRel());
    }

    @GetMapping("widgets/page/{page}")
    public CollectionModel<EntityModel<Widget>> page(@PathVariable Integer page, @RequestParam(required = false, defaultValue = "10") Integer size)
    {
        List<Widget> widgets = widgetOperationsService.getWidgets();

        if(widgets.size() == 0)
            return CollectionModel.of(widgets.stream().map(assembler::toModel).collect(Collectors.toList())).add(linkTo(methodOn(APIController.class).page(page, size)).withSelfRel());

        page = page < 1 ? 1 : page;
        size = size > 500 ? 500 : size < 1 ? 1 : size;

        int lastPageNumber = widgets.size() / size + ((widgets.size() % size) == 0 ? 0 : 1);
        page = page > lastPageNumber ? lastPageNumber : page;

        int startIndex = (page - 1) * size;
        int endIndex = startIndex + size;
        endIndex = endIndex > widgets.size() ? widgets.size() :  endIndex;

        List<Widget> widgetsPage = widgets.subList(startIndex, endIndex);
        List<EntityModel<Widget>> widgetsEntityModelList = widgetsPage.stream().map(assembler::toModel).collect(Collectors.toList());

        CollectionModel<EntityModel<Widget>> result = CollectionModel.of(widgetsEntityModelList);

        result.add(linkTo(methodOn(APIController.class).page(page, size)).withSelfRel());
        result.add(linkTo(methodOn(APIController.class).page(1, size)).withRel("first"));
        result.add(linkTo(methodOn(APIController.class).page(lastPageNumber , size)).withRel("last"));
        if((startIndex > 0) && (widgets.get(startIndex - 1) != null))
            result.add(linkTo(methodOn(APIController.class).page(page - 1, size)).withRel("prev"));
        if(endIndex < widgets.size())
            result.add(linkTo(methodOn(APIController.class).page(page + 1, size)).withRel("next"));

        return result;
    }

    @GetMapping("/widgets/{id}")
    public EntityModel<Widget> one(@PathVariable String id)
    {
        Widget widget = widgetOperationsService.getWidget(id);
        return assembler.toModel(widget);
    }

    @PostMapping("/widgets")
    public ResponseEntity<?> add(@RequestBody Widget widget)
    {
        Widget createdWidget = widgetOperationsService.createWidget(widget);
        EntityModel<Widget> entityModel = assembler.toModel(createdWidget);

        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(entityModel);
    }

    @PutMapping("/widgets/{id}")
    public ResponseEntity<?> update(@RequestBody Widget widget, @PathVariable String id)
    {
        Widget updatedWidget = widgetOperationsService.updateWidget(id, widget);
        EntityModel<Widget> entityModel = assembler.toModel(updatedWidget);
        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(entityModel);
    }

    @DeleteMapping("/widgets/{id}")
    public ResponseEntity<?> delete(@PathVariable String id)
    {
        widgetOperationsService.deleteWidget(id);
        return ResponseEntity.noContent().build();
    }


}
