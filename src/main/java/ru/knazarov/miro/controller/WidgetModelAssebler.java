package ru.knazarov.miro.controller;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import ru.knazarov.miro.controller.APIController;
import ru.knazarov.miro.model.Widget;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class WidgetModelAssebler implements RepresentationModelAssembler<Widget, EntityModel<Widget>> {

    @Override
    public EntityModel<Widget> toModel(Widget widget)
    {
        return EntityModel.of(widget,
                linkTo(methodOn(APIController.class).one(widget.getId())).withSelfRel(),
                linkTo(methodOn(APIController.class).all()).withRel("widgets"));
    }


}
